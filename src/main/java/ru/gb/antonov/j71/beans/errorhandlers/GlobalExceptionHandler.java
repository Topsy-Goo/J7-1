package ru.gb.antonov.j71.beans.errorhandlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/*   Этот класс-бин используется для обработки исключений, бросаемых в приложении. С его
помощью необрабатываемые исключения «превращаются» в сообщения и отправляются клиенту.
*/
@ControllerAdvice   //< наследуется от @Component
public class GlobalExceptionHandler
{
    @ExceptionHandler
    public ResponseEntity<?> catchResouceNotFoundException (ResourceNotFoundException e)
    {
        return new ResponseEntity<>(new ErrorMessage (e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<?> catchUnableToPerformException (UnableToPerformException e)
    {
        return new ResponseEntity<>(new ErrorMessage (e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<?> catchUserNotFoundException (UserNotFoundException e)
    {
        return new ResponseEntity<>(new ErrorMessage (e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<?> catchUserCreatingException (UserCreatingException e)
    {
        return new ResponseEntity<>(new ErrorMessage (e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<?> catchBadCreationParameterException (BadCreationParameterException e)
    {
        return new ResponseEntity<>(new ErrorMessage (e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    //это искл-е использ-ся hibernate.validator'ом.
    @ExceptionHandler
    public ResponseEntity<?> catchOurValidationException (OurValidationException e)
    {
        return new ResponseEntity<>(new ErrorMessage (e.getMessages()), HttpStatus.BAD_REQUEST);
    }
}
