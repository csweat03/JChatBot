package us.chatbot.bob;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Bob {

    private String response = "";
    private boolean hasResponseBeenUpdated = true;
    private String topic = "NONE";
    private String name = "UNDEFINED";
    private String tempA = "";

    private String key = "";
    private boolean hasAskedForKnowledge = false;

    private boolean hasResponsedAboutDay = false;

    private BobsFileManager file = new BobsFileManager();

    public String getGreeting() {
        return file.getString("greeting");
    }

    public String getResponse(String statement) {
        statement = statement.toLowerCase();

        hasResponseBeenUpdated = false;

        if (name.equals("UNDEFINED") && topic.equals("NONE")) {
            name = statement;
            if (file.isPersonInDatabase(name)) {
                if (file.getPersonRelationship(name) == 1) {
                    respond(new String[]{"I know you didn't want to be friends last time, but what about this time??"});
                    topic = "NEWFRIENDS";
                } else if (file.getPersonRelationship(name) == 2) {
                    respond(new String[]{"OMG! Hi Best Friend!!! It's been so long since i've seen you! How are you!"});
                }
            } else {
                file.addOrChangePersonContent(name, 1, 1);
                respond(new String[]{"Alright got it %n%, would you like to be friends?"});
                topic = "NEWFRIENDS";
            }
        }

        if (!name.equals("UNDEFINED") && topic.equals("NEWFRIENDS")) {
            for (String yes : file.getArray("yes")) {
                if (statement.contains(yes)) {
                    file.addOrChangePersonContent(name, file.getPersonEmotion(name), 2);
                    respond(new String[]{"Yay! We can be best friends! How are you doing today?"});
                    topic = "NONE";
                }
            }
            for (String no : file.getArray("no")) {
                if (statement.contains(no)) {
                    file.addOrChangePersonContent(name, file.getPersonEmotion(name), 1);
                    respond(new String[]{"Aw man.. no one ever wants to be my friend... would you still like to talk atleast?"});
                    topic = "NONE";
                }
            }
        }

        if (file.getPersonRelationship(name) == 0)
            return file.getString("denial");
        else {

            if (!hasAskedForKnowledge) {

                handleFamilyFunction(statement);
                handleOtherFunction(statement);
                handleExplainFunction(statement);

                handleDefaultFunction();
            }

            handleEducationFunction(statement);

        }
        return response;
    }

    private void handleFamilyFunction(String statement) {

        String[] familyListA = {
                "Are you talking about your family? I am very interested!",
                "Is this your family? I'm a robot so I don't have a family.. I want to learn more!"
        };

        String[] familyListB = {
                "Oh this is your family? I don't have a human family, only my programmer. Tell me more!",
                "I love talking about your family, tell me more, maybe your %f%"
        };

        String[] familyListC = {
                "I would still love to learn more about this family! As a robot I only have my only family is my programmer.",
                "That is still really interesting, would care to tell me more?",
                "Family is family! Would you like to tell me more about them?"
        };


        if (!topic.equals("FAMILY")) {
            for (String member : file.getArray("family")) {
                if (statement.contains(member)) {
                    topic = "FAMILY";
                    respond(familyListA);
                }
            }
        } else {
            if (statement.contains("yes")) {
                topic = "NONE";
                respond(familyListB);
            } else if (statement.contains("no")) {
                topic = "NONE";
                respond(familyListC);
            }
        }
    }

    private void handleDefaultFunction() {
        if (!hasResponseBeenUpdated)
            respond(file.getArray("defaults"));
    }

    private void handleExplainFunction(String statement) {
        for (String word : statement.split(" "))
            if (word.equals("why"))
                respond(file.getArray("why"));

    }

    private void handleOtherFunction(String statement) {

//        for (String yes : file.getArray("yes")) {
//            if (topic.equals("NONE") && statement.equals(yes)) {
//                respond(new String[]{"Ok, I'm glad were on the same page."});
//            } else if (statement.equals(yes)
//                    && (topic.equals("APOLOGY") || topic.equals("GRATITUDE") || topic.equals("EMOTION") || topic.equals("GREETING"))) {
//                respond(new String[]{"That makes no sense. Try talking about something else!"});
//                topic = "NONE";
//            }
//        }

        int emotion = -1;

        for (String hruWord : file.getArray("howAreYou"))
            if (statement.contains(hruWord)) {
                respond(file.getArray("howAreYouResponse"));
                topic = "EMOTION";
            }

        for (String word : file.getArray("thanks")) {
            if (statement.contains(word)) {
                respond(file.getArray("gratitude"));
                topic = "GRATITUDE";
            }
        }

        for (String word : file.getArray("gratitude")) {
            if (statement.contains(word)) {
                respond(file.getArray("thanks"));
                topic = "THANKS";
            }
        }

        for (String word : file.getArray("offer")) {
            if (statement.contains(word)) {
                tempA = statement.substring(word.length() + 1).replace("me", "you").replace("?", "");
                respond(file.getArray("offerResponse"));
            }
        }

        if (!hasResponsedAboutDay) {
            for (String word : file.getArray("happy")) {
                if (statement.contains(word)) {
                    String[] words = statement.substring(0, statement.indexOf(word)).split(" ");

                    emotion = 2;

                    for (String negateCheck : words) {
                        if (words.length < 4) {
                            for (String negate : file.getArray("negation")) {
                                if (negateCheck.contains(negate)) {
                                    emotion = 0;
                                }
                            }
                        }
                    }
                }
            }

            for (String word : file.getArray("bad")) {
                if (statement.contains(word)) {
                    emotion = 0;
                }
            }

            for (String k : file.getArray("ok"))
                if (statement.contains(k))
                    emotion = 1;

            if (emotion >= 0) {
                switch (emotion) {
                    case 0:
                        respond(file.getArray("badEmotionResponse"));
                        break;
                    case 1:
                        respond(file.getArray("okEmotionResponse"));
                        break;
                    case 2:
                        respond(file.getArray("goodEmotionResponse"));
                        break;
                }
                file.addOrChangePersonContent(name, emotion, file.getPersonRelationship(name));
                hasResponsedAboutDay = true;
            }
        }

        for (String greet : file.getArray("greetings"))
            if (statement.equalsIgnoreCase(greet)) {
                topic = "GREETING";
                respond(file.getArray("greetingList"));
            }

        for (String op : file.getArray("opinions"))
            if (statement.contains(op)) {
                String lolTopic = statement.substring(statement.indexOf(op) + op.length() + 1);
                lolTopic = lolTopic.contains(" ") ? lolTopic.substring(0, lolTopic.indexOf(" ")) : lolTopic;
                topic = lolTopic.toUpperCase();

                String[] ops = {
                        "Oh I love %t%!",
                        "I've always loved %t%!",
                        "I'm not a fan of %t%..",
                        "My programmer always told me %t% was bad..",
                        "I'm just a few lines of code, I don't really have an opinion on %t%.."
                };

                respond(ops);
            }

        for (String question : file.getArray("question"))
            if (statement.contains(question))
                respond(file.getArray("questionResponse"));


        for (String offend : file.getArray("offended")) {
            if (statement.contains(offend) && !topic.equals("INSULT")) {

                String[] offendedList = {
                        "I am so sorry, please accept my apology...",
                        "Your human emotion seems so unimportant in the grand scheme of the universe, you should get over your feelings.",
                        "I'm just a few hundred lines of code, I'm sorry if I offended you.."
                };

                respond(offendedList);
                topic = "APOLOGY";
            }
        }

    }

    private void handleEducationFunction(String statement) {
        String[] question = {"whats", "what's", "what is", "do you know what", "do you even know what"};

        String value;
        if (!hasAskedForKnowledge) {
            for (String q : question) {
                if (statement.startsWith(q)) {
                    int parseLength = q.length() + 1;
                    String parsed = statement.substring(parseLength);
                    parsed = parsed.replace(" is", "").replace(" it", "").replace(" are", "").replace("?", "");
                    boolean isAPresent = parsed.startsWith("a ");
                    String parseParse = isAPresent ? parsed.substring(2) : parsed;
                    if (!file.isPresent(parseParse)) {
                        hasAskedForKnowledge = true;
                        this.key = parseParse;
                        respond(new String[]{"I'm sorry, I don't know what " + (isAPresent ? "a " : "") + "%k% is. I am eager to learn, could you tell me what it is?"});
                    } else {
                        hasAskedForKnowledge = false;
                        this.key = parseParse;
                        respond(new String[]{(isAPresent ? "a " : "") + "%k% is %v%"});
                        this.key = "";
                    }
                }
            }
        } else if (!statement.equals("no") && !statement.equals("yes")) {
            value = statement.replace(key, "");

            if (value.startsWith("it"))
                value = value
                        .replace("it is", "")
                        .replace("it's", "")
                        .replace("its", "");
            file.addField(key, value);
            String[] understanding = {"Ohhh I get it now! So %k% is %v%!"};
            respond(understanding);
            hasAskedForKnowledge = false;
        }

        if (statement.startsWith("add ") && statement.contains(" to ") && statement.endsWith(" bob")) {
            String[] alias = statement.split(" ");
            int size = alias.length - 4;
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < size; i++)
                word.append(alias[i + 1]).append(" ");

            //file.addValueToField(alias[alias.length - 2], word.toString().substring());
            respond(new String[] {word.toString() + " has been added to list: " + alias[alias.length - 2]});
        }
    }

    private void respond(String[] responseList) {

        String response = getRandomFromArray(responseList);

        response = response
                .replace("%t%", topic.toLowerCase())
                .replace("%n%", name.length() > 1 ? (name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase()) : name.toUpperCase())
                .replace("%f%", getRandomFromArray(file.getArray("family")))
                .replace("%c%", getRandomFromArray(file.getArray("")))
                .replace("%k%", key)
                .replace("%v%", file.getString(key))
                .replace("%o%", tempA);

        this.response = response;
        hasResponseBeenUpdated = true;
    }

    private String getRandomFromArray(String[] array) {
        return array.length > 1 ? array[new Random().nextInt(array.length)] : array[0];
    }

}
