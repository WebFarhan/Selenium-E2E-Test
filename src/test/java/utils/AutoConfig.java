package utils;

public class AutoConfig {
    String chromeDriverPath;
    String url;
    String advocateName;
    String advocateEmail;
    String advocatePassword;
    String advocateMessage;
    String guestMessage;
    String chatEndReason;

    public String getAdvocateMessage() {
        return advocateMessage;
    }

    public String getGuestMessage() {
        return guestMessage;
    }

    public String getAdvocateName() {
        return advocateName;
    }

    public String getChromeDriverPath() {
        return chromeDriverPath;
    }

    public String getUrl() {
        return url;
    }

    public String getAdvocateEmail() {
        return advocateEmail;
    }

    public String getAdvocatePassword() {
        return advocatePassword;
    }

    public String getChatEndReason() { return chatEndReason; }

    /**
     * Configuration settings for e2e test
     * @param chromeDriverPath File path location of driver. Download <a href="https://googlechromelabs.github.io/chrome-for-testing/">here</a>
     * @param url Elm URL
     * @param advocateEmail Test advocate account email
     * @param advocatePassword Test advocate account password
     * @param advocateName Test advocate nickname/name to lookup (from guest POV)
     * @param advocateMessage Message to send in chat from advocate
     * @param guestMessage Message to send in chat from guest
     * @param chatEndReason Advocate post chat survey part 3 - chat topic (text copy, not alias)
     */
    public AutoConfig (
        String chromeDriverPath,
        String url,
        String advocateEmail,
        String advocatePassword,
        String advocateName,
        String advocateMessage,
        String guestMessage,
        String chatEndReason
    ) {
        this.chromeDriverPath = chromeDriverPath;
        this.url = url;
        this.advocateEmail = advocateEmail;
        this.advocatePassword = advocatePassword;
        this.advocateName = advocateName;
        this.advocateMessage = advocateMessage;
        this.guestMessage = guestMessage;
        this.chatEndReason = chatEndReason;
    }
}
