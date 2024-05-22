package model;

//Lesson that contains a video
public class VideoLesson extends Lesson {
    private String videoLink;

    public VideoLesson(int lessonId, String lessonTitle, String lessonDescription, String videoLink) {
        super(lessonId, lessonTitle, lessonDescription);
        this.videoLink = videoLink;
    }

    public VideoLesson(VideoLesson videoLesson) {
        super(videoLesson);
        this.videoLink = videoLesson.videoLink;
    }

    //getters and setters
    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    @Override
    public int takeLesson() {
        System.out.println(lessonTitle);
        System.out.println(lessonDescription);
        System.out.println("Video: " + videoLink);
        return 100;
    }

    @Override
    public Lesson copyLesson() {
        return new VideoLesson(this);
    }
}
