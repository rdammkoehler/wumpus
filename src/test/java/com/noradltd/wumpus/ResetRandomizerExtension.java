package com.noradltd.wumpus;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ResetRandomizerExtension implements Extension, AfterTestExecutionCallback, BeforeTestExecutionCallback {
    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        Helpers.resetRandomizer();
    }

    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        Helpers.resetRandomizer();
    }
}
