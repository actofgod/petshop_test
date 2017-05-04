package petshop_test;

import java.util.ArrayList;
import java.util.List;

public class FailureResponse extends AbstractResponse
{
    private List<ResponseErrorMessage> reason;

    public FailureResponse(String errorMessage)
    {
        this(errorMessage, -1);
    }

    public FailureResponse(String errorMessage, int code)
    {
        super(false);
        reason = new ArrayList<>();
        reason.add(new ResponseErrorMessage(errorMessage, code));
    }

    public List<ResponseErrorMessage> getReason()
    {
        return reason;
    }
}
