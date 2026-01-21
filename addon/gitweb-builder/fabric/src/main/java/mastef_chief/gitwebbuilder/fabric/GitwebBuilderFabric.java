package mastef_chief.gitwebbuilder.fabric;

import com.ultreon.devices.fabric.FabricApplicationRegistration;
import mastef_chief.gitwebbuilder.GitwebBuilder;
import net.fabricmc.api.ModInitializer;

public class GitwebBuilderFabric implements ModInitializer, FabricApplicationRegistration {
	@Override
	public void registerApplications() {
		GitwebBuilder.registerApplications();
	}

	@Override
	public void onInitialize() {
		new GitwebBuilder();
	}
}
