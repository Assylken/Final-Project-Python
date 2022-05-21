package kz.adilet.kazakhlearn.Model;

public class Users {
    private String username, phone, image, city, socialNetwork;
    private boolean admin;

    public Users() {

    }

    public Users(String username, String phone, String image, String city, boolean admin, String socialNetwork) {
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.city = city;
        this.admin = admin;
        this.socialNetwork = socialNetwork;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String socialNetwork) {
        this.city = socialNetwork;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(String socialNetwork) {
        this.socialNetwork = socialNetwork;
    }
}


