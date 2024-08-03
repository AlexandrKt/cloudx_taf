package aws.sns_sqs.sns;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.search.FlagTerm;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailHelper {
    public Message[] checkInboxForSubscriptionConfirmation(String host, String storeType, String user, String password) {
        try {
            // Set up properties for IMAP
            Properties properties = new Properties();
            properties.put("mail.imap.host", host);
            properties.put("mail.imap.port", "993");
            properties.put("mail.imap.starttls.enable", "true");

            // Get the session object
            Session emailSession = Session.getDefaultInstance(properties);

            // Create the IMAP store object and connect
            Store store = emailSession.getStore("imaps");
            store.connect(host, user, password);

            // Access the inbox folder
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            // Search for unread emails
            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            return messages;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String extractConfirmationLink(Message[] messages) throws MessagingException, IOException {
        // Regex pattern to match the confirmation link
        String linkRegex = "https://sns\\.[^\\s]+amazonaws\\.com[^\\s]+";
        Pattern pattern = Pattern.compile(linkRegex);

        for (Message message : messages) {
            if (message.getSubject().contains("AWS Notification")) {
                String content = getTextFromMessage(message);
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    return matcher.group(0); // Return the first matching link
                }
            }
        }
        throw new RuntimeException("Confirmation link not found in the provided messages.");
    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        if (message.isMimeType("text/plain")) {
            return message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            return getTextFromMimeMultipart(mimeMultipart);
        }
        return "";
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        StringBuilder result = new StringBuilder();
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent());
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result.append(Jsoup.parse(html).text()); // Using Jsoup to convert HTML to plain text
            }
        }
        return result.toString();
    }
}
