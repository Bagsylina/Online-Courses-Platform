# Online Courses Platform

- Courses that consist of multiple lsseons and a final quiz
- Lessons can be of diferent types: text, video, quiz or task
- Course grade si 70% lesson score (50% quizzes + 30% tasks + 20% other) + 30% final quiz
- Courses can be of multiple categories (ex.: programming, art, music, cooking, economy, language)
- Multiple users, progress for each enrolled lesson is tracked
- Connection with localhost database using JDBC, saving course, lesson, user and enrolled courses data
- App Service with multiple functions: adding courses/users, user creation form, basic login, suggesting courses, top users by score, enroll, take and rate a course, view basic user profile, search courses by tag
- All data inserted and actions taken are saved in a log and able to be generated into a csv file
- Exception handling and custom exceptions
