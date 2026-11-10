package better.anticheat.paper;

import better.anticheat.core.util.dependencies.DependencyList;
import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;

/* Modified: Maven Central (Paper's mirror) only. */
public class BetterAnticheatPaperLoader implements PluginLoader {
    @Override
    public void classloader(final PluginClasspathBuilder classpathBuilder) {
        final var resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build());
        for (final var dependencyString : DependencyList.DEPENDENCIES) {
            resolver.addDependency(new Dependency(new DefaultArtifact(dependencyString), null));
        }
        classpathBuilder.addLibrary(resolver);
    }
}
