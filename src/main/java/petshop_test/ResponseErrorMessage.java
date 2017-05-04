package petshop_test;

public class ResponseErrorMessage
{
    private int code;
    private String message;

    public ResponseErrorMessage(String message)
    {
        this(message, -1);
    }

    public ResponseErrorMessage(String message, int code)
    {
        this.message = message;
        this.code = code;
    }

    public int getCode()
    {
        return code;
    }

    public String getMessage()
    {
        return message;
    }
}
