package com.ultreon.devices.core;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.io.MessageTransport;

public class ValueSharingExample {
    public static void main(String[] args) {
        Engine engine = Engine.newBuilder("python")
                .build();

        try (Context context1 = Context.newBuilder()
                .engine(engine)
                .allowValueSharing(true)
                .allowPolyglotAccess(PolyglotAccess.newBuilder().allowBindingsAccess("python").build())
                .allowHostAccess(HostAccess.ALL) // ✅ Required for polyglot imports/exports
                .build();
             Context context2 = Context.newBuilder()
                     .engine(engine)
                     .allowValueSharing(true)
                     .allowPolyglotAccess(PolyglotAccess.newBuilder().allowBindingsAccess("python").build())
                     .allowHostAccess(HostAccess.ALL) // ✅ Required for polyglot imports/exports
                     .build();
        ) {
            // Python imports the shared value and modifies it
            context1.eval("python", """
                    import sys
                    sys.exit = lambda x: x
                    """);

            context2.eval("python", """
                    import sys
                    print(sys.exit)
                    sys.exit = 2
                    """);

            context1.eval("python", """
                    import sys
                    print(sys.exit)
                    """);

            // Retrieve updated value from Java
            Value updatedValue = context1.getBindings("python").getMember("shared_value");

            // Print the updated value in Java
            System.out.println("Updated in Java: " + updatedValue.getMember("msg").asString());
        }

        engine.close();
    }
}