package mastef_chief.gitwebbuilder;

import com.ultreon.devices.api.ApplicationManager;
import com.ultreon.devices.api.task.TaskManager;
import dev.architectury.core.item.ArchitecturyRecordItem;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import mastef_chief.gitwebbuilder.app.GWBApp;
import mastef_chief.gitwebbuilder.app.models.GWBLogoModel;
import mastef_chief.gitwebbuilder.app.tasks.TaskNotificationCopiedCode;
import mastef_chief.gitwebbuilder.app.tasks.TaskNotificationCopiedLink;
import net.minecraft.resources.ResourceLocation;

public class GitwebBuilder {

    public static GitwebBuilder INSTANCE;

    public GitwebBuilder() {
        INSTANCE = this;
        TaskManager.registerTask(TaskNotificationCopiedCode::new);
        TaskManager.registerTask(TaskNotificationCopiedLink::new);

        EntityModelLayerRegistry.register(GWBLogoModel.LAYER_LOCATION, GWBLogoModel::createTexturedModelData);
    }

    public static void registerApplications() {
        ApplicationManager.registerApplication(new ResourceLocation(Reference.MOD_ID, "gitwebbuilder_app"), () -> GWBApp::new, false);
    }
}
