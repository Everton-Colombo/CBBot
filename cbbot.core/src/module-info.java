module cbbot.core {
	requires transitive cbbot.common;

	// Selenium Requirements
	requires transitive net.bytebuddy;
	requires transitive commons.exec;
	requires transitive okhttp3;
	requires transitive com.google.common;
	requires transitive okio;
	requires transitive org.openqa.selenium; // There is no error. IntelliJ is just not recognizing the package, for some reason.

	exports cbbot.core;
	exports cbbot.core.controllers;
}