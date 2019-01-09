package alex.task_manager.models;


import com.google.gson.annotations.SerializedName;

public class DefaultResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    public DefaultResponse(boolean err, String msg) {
        this.success = err;
        this.message = msg;
    }

    public boolean isError() {
        return !success;
    }
    public boolean isSuccess() {
        return success;
    }

    public String getMsg() {
        return message;
    }
}
