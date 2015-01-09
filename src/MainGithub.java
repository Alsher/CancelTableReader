import org.apache.commons.net.util.Base64;

import java.io.IOException;
import java.util.Scanner;

public class MainGithub
{
    public static void main(String[] args) throws InterruptedException, IOException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bitte Username eingeben: ");
        String username = scanner.next();
        System.out.println("Bitte Passwort eingeben: ");
        String password = scanner.next();
        System.out.println("Bitte ersten url part eingeben");
        String urlp1 = scanner.next();
        System.out.println("Bitte zweiten url part eingeben");
        String urlp2 = scanner.next();
        scanner.close();

        AccessPage.getPage(new String(Base64.encodeBase64((username + ":" + password).getBytes())), urlp1, urlp2);
        new GUI();
    }
}
