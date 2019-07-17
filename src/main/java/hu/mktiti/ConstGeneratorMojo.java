package hu.mktiti;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.*;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Generate constant util class/object based on compile time maven properties (e.g. version number, build time, etc.)
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ConstGeneratorMojo extends AbstractMojo {

    enum Type {
        java(JavaPrinter::new),
        kotlin(KotlinPrinter::new);

        private final ConstPrinter.Factory factory;

        Type(final ConstPrinter.Factory factory) {
            this.factory = factory;
        }

        ConstPrinter getPrinter(final PrinterConf conf) {
            return factory.apply(conf);
        }
    }

    /**
     *  The directory which the code generation will target.
     *  By default ${project.build.directory}/src/main (usually target/src/main)
     *  Depending on the generation type, a subdirectory (java/ or kotlin/) will
     *  be created that should be added to your build path.
     */
    @Parameter(defaultValue = "${project.build.directory}/src/main", property = "outputDir", required = true)
    private File outputDirectory;

    /**
     *  Type of the generated constant file, 'java' for java static util, kotlin for kotlin object.
     *  Use whichever language is present in your project, if both are used it should be easy to access
     *  the constants anyhow.
     *
     *  Defaults to java.
     */
    @Parameter(defaultValue = "java", property = "type", required = true)
    private Type type;

    /**
     *  Name of the package in which the constant file will be generated.
     *  If kotlin type is chosen, the actual directory hierarchy will not track the package,
     *  but will rather be a single subdirectory.
     */
    @Parameter(property = "package", required = true)
    private String packageName;

    /**
     *  Name of the class or object to be generated.
     *
     *  Defaults to ProjectInfo.
     */
    @Parameter(property = "classname", defaultValue = "ProjectInfo", required = true)
    private String className;

    /**
     *  Visibility of the generated class/objects and its members.
     *  One of PUBLIC, PACKAGE_PRIVATE, or DEFAULT
     *
     *  On java, the modifiers match one-to-one, with kotlin PACKAGE_PRIVATE matches to internal.
     *
     *  For the class/object, if the given modifier is invalid, the default will be used.
     *
     *  Defaults to PUBLIC.
     */
    @Parameter(property = "visibility", defaultValue = "PUBLIC", required = true)
    private PrinterConf.Visibility visibility;

    /**
     *  Tells the generator to generate static getter methods rather than static fields.
     *  On kotlin, when accessed from kotlin code it doesn't matter, but when accessed from java the effect is the same
     *  (i.e static getters of static fields are generated).
     *
     *  Defaults to false (fields).
     */
    @Parameter(property = "useGetters", defaultValue = "false", required = true)
    private boolean useGetters;

    /**
     *  Key-Value list of values to be made into constant fields. Empty values will be converted to empty strings.
     *  Keys must be valid identifiers for the given language (e.g. no hard keywords, no starting with digit, etc.).
     *
     */
    @Parameter(property = "values")
    private Map<String, String> values;


    /**
     *  Sets whether the project version should be added, by default it is added as it is the most used config.
     *
     *  Defaults to true.
     */
    @Parameter(property = "addVersion", defaultValue = "true", required = true)
    private boolean addVersion;

    /**
     *  Only used to pass project version. Can be set manually, but it is easier to disable version adding.
     */
    @Parameter(property = "version", defaultValue = "${project.version}", readonly = true)
    private String version;

    public ConstGeneratorMojo() { }

    public void execute() throws MojoExecutionException {
        final Log log = getLog();

        final PrinterConf conf = new PrinterConf(
                packageName,
                className,
                "Generated by const-generator-maven-plugin at " + LocalDateTime.now(),
                visibility,
                useGetters
        );
        final ConstPrinter printer = type.getPrinter(conf);

        final Map<String, String> variables = new HashMap<>();
        if (addVersion) {
            variables.put("version", version);
        }
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (printer.isValid(entry.getKey())) {
                final String value = (entry.getValue() == null) ? "" : entry.getValue();
                variables.put(entry.getKey(), value);
            } else {
                throw new MojoExecutionException("Value name '" + entry.getKey() + "' is not a valid identifier for type '" + type + "'");
            }
        }

        log.info("Values to add:");
        for (Map.Entry<String, String> value : variables.entrySet()) {
            log.info("    " + value.getKey() + " = " + Util.escapeString(value.getValue()));
        }

        final File genDir = Paths.get(outputDirectory.getAbsolutePath(), printer.getDirPath()).toFile();
        if (!genDir.exists() && !genDir.mkdirs()) {
            throw new MojoExecutionException("Target directory doesn't exist");
        }

        final File constFile = new File(genDir, printer.getFilename());
        log.info("Creating constant file at " + constFile.getPath());

        try (final PrintWriter writer = new PrintWriter(new FileOutputStream(constFile))) {
            printer.print(variables, writer);
        } catch (final FileNotFoundException fnfe) {
            throw new MojoExecutionException("Output file cannot be accessed");
        }

        log.info("Const file created!");
    }
}
