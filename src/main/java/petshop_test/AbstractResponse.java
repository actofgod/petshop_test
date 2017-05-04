package petshop_test;

abstract public class AbstractResponse
{
    private boolean success;

    public AbstractResponse()
    {
        success = true;
    }

    public AbstractResponse(boolean success)
    {
        this.success = success;
    }

    public boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(boolean value)
    {
        success = value;
    }
}
