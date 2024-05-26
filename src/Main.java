import repository.UserCourses;
import service.AppService;
import model.*;

import java.util.*;

public class Main {
    public static void main(String[] args){
        /*
        Online Courses Platform
        - Courses that consist of multiple lsseons and a final quiz
        - Lessons can be of diferent types: text, video, quiz or task
        - Course grade si 70% lesson score (50% quizzes + 30% tasks + 20% other) + 30% final quiz
        - Courses can be of multiple categories (ex.: programming, art, music, cooking, economy, language)
        - Multiple users, progress for each enrolled lesson is tracked
         */
        AppService app = AppService.getInstance();
        app.clearDatabase();
        User user1 = new User("GuardiaN", "Andrei", "Smadu", "andrei.smadu@gmail.com", "123456",
                new ArrayList<>(Arrays.asList(Tag.PROGRAMMING, Tag.MUSIC, Tag.ECONOMY)));
        User user2 = new User("fasolica", "Andrei David", "Coman", "andrei.coman@gmail.com", "123456",
                new ArrayList<>(Arrays.asList(Tag.PROGRAMMING, Tag.COOKING, Tag.MATH, Tag.SCIENCE)));
        User user3 = new User("eduard", "Eduard", "Burlacu", "eduard@gmail.com", "123456",
                new ArrayList<>(Arrays.asList(Tag.FITNESS, Tag.LITERATURE, Tag.COOKING)));
        app.addUser(user1);
        app.addUser(user2);
        app.addUser(user3);
        QuizQuestion question1 = new QuizQuestion("Care este portul pentru DNS?", new String[]{"0", "22", "53", "51"}, 2);
        QuizQuestion question2 = new QuizQuestion("Cum salvezi configuratia curenta pe un device?",
                new String[]{"copy running-config startup-config", "save config", "copy startup-config running-config", "save running-config"}, 0);
        QuizQuestion question3 = new QuizQuestion("Care este masca de retea corespunzatoare pentru 63 de host-uri?",
                new String[]{"255.255.255.224", "255.255.255.0", "255.255.255.192", "255.255.255.128"}, 3);
        List <QuizQuestion> questionsList = new ArrayList<>(Arrays.asList(question1, question2, question3));
        Quiz quiz1 = new Quiz(1, "Test Packet Tracer", "Testeaza-ti cunostintele despre retele.", questionsList);
        Task task1 = new Task(2, "Configurarea unei retele", "Configureaza o retea intreaga",
                new ArrayList<>(Arrays.asList("Configureaza un PC HOST", "Configureaza un switch", "Configureaza un router",
                        "Configureaza switch-ul din reteaua server-ului", "Configureaza server-ul")));
        VideoLesson videoLesson1 = new VideoLesson(3, "Introducere in Packet Tracer", "Curs introductiv",
                "https://www.youtube.com/watch?v=qZB_biPOBwA");
        TextLesson textLesson1 = new TextLesson(4, "Curs Introductiv", "Introductie",
                new ArrayList<>(Arrays.asList("Pagina 1", "Pagina 2", "Pagina 3")));
        Course course = new Course(100, "Retele", "Curs despre configurarea retelelor folosing Packet Tracer",
                quiz1, Tag.PROGRAMMING);
        course.addLesson(textLesson1);
        course.addLesson(videoLesson1);
        course.addLesson(task1);
        app.addCourse(course);
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.println("Select action: ");
            System.out.println("0: Create User");
            System.out.println("1: Login");
            System.out.println("2: Suggest Courses");
            System.out.println("3: Top Users");
            System.out.println("4: Enroll In A Course");
            System.out.println("5: Display User Profile");
            System.out.println("6: Search Tag");
            System.out.println("7: Take Course");
            System.out.println("8: Rate Course");
            System.out.println("9: Get Course");
            System.out.println("10: Get User");
            System.out.println("11: Generate Audit");
            System.out.println("12: Search Lessons by Tag");
            System.out.println("13: Courses for all users");
            int chosen = scanner.nextInt();
            switch(chosen) {
                case 0:
                    app.createUser();
                    break;
                case 1:
                    System.out.println("Username: ");
                    String username = scanner.next();
                    System.out.println("Password: ");
                    String password = scanner.next();
                    app.login(username, password);
                    break;
                case 2:
                    System.out.println(app.suggestCourses());
                    break;
                case 3:
                    System.out.println(app.topUsers());
                    break;
                case 4:
                    System.out.println("Enter course id:");
                    int courseId = scanner.nextInt();
                    app.enrollInCourse(courseId);
                    break;
                case 5:
                    System.out.println("Enter username:");
                    String username1 = scanner.next();
                    app.displayUserProfile(username1);
                    break;
                case 6:
                    System.out.println("Enter tag:");
                    String tagName = scanner.next();
                    Tag tag = Tag.valueOf(tagName);
                    System.out.println(app.searchTag(tag));
                    break;
                case 7:
                    System.out.println("Enter course id:");
                    int courseId1 = scanner.nextInt();
                    app.takeCourse(courseId1);
                    break;
                case 8:
                    System.out.println("Enter course id:");
                    int courseId2 = scanner.nextInt();
                    System.out.println("Enter rating:");
                    int rating = scanner.nextInt();
                    app.rateCourse(courseId2, rating);
                    break;
                case 9:
                    System.out.println("Enter course id:");
                    int courseId3 = scanner.nextInt();
                    System.out.println(app.getCourse(courseId3));
                    break;
                case 10:
                    System.out.println("Enter username:");
                    String username2 = scanner.next();
                    System.out.println(app.getUser(username2));
                    break;
                case 11:
                    app.generateAudit();
                    break;
                case 12:
                    System.out.println("Enter tag:");
                    String tagName1 = scanner.next();
                    Tag tag1 = Tag.valueOf(tagName1);
                    List<String> lessons = app.getLessonsByTag(tag1);
                    for(String lesson: lessons)
                        System.out.println(lesson);
                    break;
                case 13:
                    List<UserCourses> userCourses = app.getUserCourses();
                    for(UserCourses userCourse: userCourses)
                        System.out.println(userCourse);
                    break;
                default:
                    System.out.println("Invalid action");
                    break;
            }
        }
    }
}