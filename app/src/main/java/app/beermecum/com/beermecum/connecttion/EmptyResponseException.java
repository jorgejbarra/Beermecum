package app.beermecum.com.beermecum.connecttion;

/**
 * Created by Mormeguil on 28/03/2015.
 */
public class EmptyResponseException extends Exception {

    public EmptyResponseException() {
    }

    public EmptyResponseException(String detailMessage) {
        super(detailMessage);
    }

    public EmptyResponseException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public EmptyResponseException(Throwable throwable) {
        super(throwable);
    }
}
