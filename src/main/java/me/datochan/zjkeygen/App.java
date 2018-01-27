package me.datochan.zjkeygen;

import me.datochan.zjkeygen.utils.Base64Coder;
import me.datochan.zjkeygen.utils.Encryption;

import java.io.Console;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
    private String edition;
    private String licenseType;
    private String description;
    private String licenseId;
    private String purchaseDate;
    private String maintenanceExpiryDate;
    private String licenseExpiryDate;
    private String evaluationType;
    private String unlimitedUsersFlag;
    private String maximumNumberOfUsers;
    private String organisation;
    private String organisationId;
    private String organisationEmail;

    public App(String username, String email, String organisation, String serverId) {
        this.edition = "2";
        this.licenseType = "1";   // 商业版本
        this.description = "Zephyr for JIRA - Test Management";
        this.licenseId = serverId;
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        this.purchaseDate = sdf.format(new Date());

        this.evaluationType = "0";   // 是否试用评估版本
        this.maintenanceExpiryDate = "09-29-2099";
        this.licenseExpiryDate = "09-29-2099";
        this.unlimitedUsersFlag = "1";
        this.maximumNumberOfUsers = "9999";
        this.organisationId = username;
        this.organisation = organisation;
        this.organisationEmail = email;
    }

    public String toString() {
        return String.format("%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s", this.edition, this.licenseType,
                this.description, this.licenseId, this.purchaseDate, this.maintenanceExpiryDate, this.licenseExpiryDate,
                this.evaluationType, this.unlimitedUsersFlag, this.maximumNumberOfUsers, this.organisation,
                this.organisationId, this.organisationEmail);
    }

    /**
     * Use  java.io.console to read data from console
     *
     * @param prompt
     * @return input string
     */
    private static String readDataFromConsole(String prompt) throws IllegalStateException{
        Console console = System.console();
        if (console == null) {
            throw new IllegalStateException("Console is not available!");
        }
        return console.readLine(prompt);
    }

    public static void main(String[] args) throws Exception {
//        String username = "cracker";
//        String email = "cracker@U&MTeam.org";
//        String organisation = "U&M Team";
//        String serverId = "BS4K-UDMP-98GQ-GRZ2";
        String username = readDataFromConsole("Username: ");
        String email = readDataFromConsole("Email: ");
        String organisation = readDataFromConsole("Organisation: ");
        String serverId = readDataFromConsole("ServerId: ");

        Encryption encrypt = new Encryption();
        encrypt.generateKeys();

        byte[] enckey = encrypt.getPublic().getEncoded();
        String strPubKey = new String(Base64Coder.encode(enckey));


        App newTest = new App(username, email, organisation, serverId);
        String strSign = encrypt.sign(newTest.toString());

        System.out.println("-------Begin-----------------------");
        System.out.println("");
        System.out.printf("%s#%s@%s\n", Base64Coder.encode(newTest.toString()), strSign, strPubKey);
        System.out.println();
        System.out.println("-------End-----------------------");
    }


}