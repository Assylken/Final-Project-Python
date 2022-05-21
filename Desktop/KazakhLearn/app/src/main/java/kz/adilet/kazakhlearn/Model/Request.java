package kz.adilet.kazakhlearn.Model;

public class Request {
    private String id, word, transcript, image, searchMean;

    public Request() {

    }

    public Request(String id, String word, String transcript, String image, String mMeanings) {
        this.id = id;
        this.word = word;
        this.transcript = transcript;
        this.image = image;
        this.searchMean = mMeanings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSearchMean() {
        return searchMean;
    }

    public void setSearchMean(String searchMean) {
        this.searchMean = searchMean;
    }
}
