import us.chatbot.bob.Bob;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Bob bob = new Bob();

        System.out.println(bob.getGreeting());

        while (true) System.out.println(bob.getResponse(new Scanner(System.in).nextLine()));
    }

}
