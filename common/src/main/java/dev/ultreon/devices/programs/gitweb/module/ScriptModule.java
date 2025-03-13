package dev.ultreon.devices.programs.gitweb.module;

import dev.ultreon.devices.api.app.Layout;
import dev.ultreon.devices.debug.DebugLog;
import dev.ultreon.devices.programs.gitweb.component.GitWebFrame;
import dev.ultreon.devices.programs.gitweb.layout.ModuleLayout;

import java.util.Map;

@Deprecated
public class ScriptModule extends Module {
    @Override
    public String[] getRequiredData() {
        return new String[]{"script", "runtime"};
    }

    @Override
    public String[] getOptionalData() {
        return new String[0];
    }

    @Override
    public int calculateHeight(Map<String, String> data, int width) {
        return 1;
    }

    @Override
    public void generate(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {
        var script = data.get("script").replaceAll("scriptModule:newLine", "\n").replaceAll("scriptModule:equals", "=");
        if (data.get("runtime").equals("apoint")) {
            initAPointRuntime(frame);
            DebugLog.log(script);
//            frame.aPointRuntime.parse(script);
        } else if (data.get("runtime").equals("ulang")) {
            //
        }
    }

    public static void main(String[] args) {
        var script = """
                VAL str paragraph
                VAL str text
                VAL str "Hello World!"
                MODIFY
                """;
        DebugLog.log(script.replaceAll("\n", "scriptModule:newLine").replaceAll("=", "scriptModule:equals"));
    }

    private void initAPointRuntime(GitWebFrame frame) {
//        if (frame.aPointRuntime == null) {
//            APointRuntime runtime = APoint.createRuntime();
//            runtime.removeCommand("METHOD:STATIC");
//            runtime.removeCommand("METHOD");
//            runtime.removeCommand("FIELD:STATIC");
//            runtime.addCommand("RANDOM", (params) -> {
//                runtime.setPointer(runtime.getOut(), Math.random());
//            });
//            runtime.addCommand("MODIFY", (params) -> {
//                String id = (String) runtime.getVals().get(0);
//                String key = (String) runtime.getVals().get(1);
//                String value = (String) runtime.getVals().get(2);
//                for (Component component : frame.layout.components) {
//                    if (component instanceof ModuleLayout moduleLayout) {
//                        if (id.equals(moduleLayout.entry.getId())) {
//                            var newData = new HashMap<>(moduleLayout.entry.getData());
//                            newData.put(key, value);
//                            moduleLayout.entry.setData(newData);
//                            moduleLayout.modify();
//                        }
//                    }
//                }
//            });
//            frame.aPointRuntime = runtime;
//            //runtime.removeCommand("");
//        }
    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

    @Override
    public void tick(GitWebFrame frame, Layout layout, int width, Map<String, String> data) {

    }

    @Override
    public void modify(GitWebFrame frame, ModuleLayout layout, int width, Map<String, String> data) {
        this.generate(frame, layout, width, data);
    }
}
