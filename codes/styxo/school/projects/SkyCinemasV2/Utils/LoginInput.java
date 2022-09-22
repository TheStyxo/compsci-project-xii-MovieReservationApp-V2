package codes.styxo.school.projects.SkyCinemasV2.Utils;

//Class to handle user input for login
public class LoginInput {
    public final UserSearchKeyType type;
    public final String input;

    public LoginInput(String input) {
        this.input = input;
        if (Utils.isValidPhone(input))
            this.type = UserSearchKeyType.PHONE;
        else if (Utils.isValidEmail(input))
            this.type = UserSearchKeyType.EMAIL;
        else if (Utils.isValidUsername(input))
            this.type = UserSearchKeyType.USERNAME;
        else
            this.type = UserSearchKeyType.INVALID;
    }
}
