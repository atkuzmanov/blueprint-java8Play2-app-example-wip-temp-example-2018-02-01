def package(self):
    specfile = self.spec
    specfile.set_build_arch("x86_64")
    default_jar_file_location_example = self.root +"default-example-target/scala-2.11/" + self.name + ".jar"
    default_init_d_script_location_example = self.root +"initd_script.sh"

    # Copy the required files in the source "src/" directory.
    self.run(["cp", "-R", default_init_d_script_location_example, self.sources_dir])
    self.run(["cp", "-R", default_jar_file_location_example, self.sources_dir])

    # Add required files as sources in the specfile.
    source = specfile.add_source(self.name + ".jar")
    init_d_script = specfile.add_source("initd_script.sh")

    # Adding required install steps.
    specfile.add_install_steps([
        ["mkdir", "-p", "%{buildroot}/usr/lib/" + self.name],
        ["cp", "-R", source, "%{buildroot}/usr/lib/" + self.name],
        ["mkdir", "-p", "%{buildroot}%{_initddir}"],
        ["cp", "-R", init_d_script, "%{buildroot}%{_initddir}/" + self.name]
    ])

    # Add the required files permissions.
    specfile.add_files(["/usr/lib/" + self.name])
    self.spec.add_files(
        ["%{_initddir}/" + self.name],
        file_permissions=755,
        dir_permissions=755
    )


