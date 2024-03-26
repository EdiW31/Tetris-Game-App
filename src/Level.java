public class Level {
    private final Score score;

    public Level(Score score) {
        this.score = score;
    }

    public int calculateLevel() {
        int currentScore = score.getScore();

        if (currentScore < 400) {
            return 1;
        } else if (currentScore < 800) {
            return 2;
        } else if (currentScore < 1200) {
            return 3;
        } else if (currentScore < 1500) {
            return 4;
        } else if (currentScore < 2000) {
            return 5;
        } else if (currentScore < 2500) {
            return 6;
        } else if (currentScore < 3000) {
            return 7;
        }else if (currentScore < 4000) {
            return 8;
        }else if (currentScore < 5000) {
            return 9;
        }else return 10;
    }
}
