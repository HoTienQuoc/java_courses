package solid_principles_overview_single_responsibility_principle.solution;

public class SecurityService {

    public boolean hasAccess(User user) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        } else {
            return false;
        }
    }

}
