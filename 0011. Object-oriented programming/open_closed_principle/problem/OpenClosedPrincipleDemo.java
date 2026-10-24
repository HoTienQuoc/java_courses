package open_closed_principle.problem;

public class OpenClosedPrincipleDemo {

    public static void main(String[] args) {
        LoanHandler loanHandler = new LoanHandler();
        User user = new User();

        loanHandler.approvePersonalLoan(user);
        loanHandler.approveMortgage(user);

    }

}
