package MODEL.networking;

/**
 * Classe pour encapsuler le résultat d'une opération OBEP
 */
public class ResultatBSPP
{
    private boolean success;
    private String message;

    public ResultatBSPP()
    {
        this.success = false;
        this.message = "";
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return "ResultatOBEP{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
