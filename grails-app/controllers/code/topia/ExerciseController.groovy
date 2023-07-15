package code.topia
import org.slf4j.LoggerFactory

class PerformAttemptParam {
    static final int MIN_ID = 1
    int     exerciseId
    String  answer

    static constraints = {
        exerciseId      min: MIN_ID
        answer          nullable: false, blank: false
    }
}

class ExerciseController {

    def userService
    def exerciseService
    def logger = LoggerFactory.getLogger(getClass())

    def index() { 
        if (session?.user_logged_id && session.user_logged_id > 0) {
            // Logueado
            try {
                logger.info("[ExerciseController] buscamos el usuario")
                User user = userService.getUserById(session.user_logged_id)
                logger.info("[ExerciseController] usuario recuperado: ${user}")
                Exercise exercise = exerciseService.getExercise(params.exerciseId.toInteger())
                logger.info("[ExerciseController] al buscar el ejercicio: ${params} - ${exercise.id}")
                render(view: "index", model: [user: user, exercise: exercise])
                return
            } catch (UserNotExistException e) {
                // no deberiamos entrar aca
                logger.error("[ExerciseController] Usuario no existe; error: ${e}")
                redirect(controller: 'user', action: 'index')
                return
            } catch (ExerciseNotExistException e) {
                // no deberiamos entrar aca
                logger.error("[ExerciseController] Ejercicio no existe; error: ${e}")
                redirect(controller: 'home', action: 'index')
                return
            }
            
        }
        // No esta logueado o no existe
        redirect(controller: 'user', action: 'index')
    }

    def performAttempt(PerformAttemptParam p) {
        logger.info("[ExerciseController] attempt param recibido: ${p.exerciseId} - ${p.answer}")
        try {
            logger.info("[ExerciseController] Realizamos un intento de ejercicio")
            userService.performAttempt((int)session.user_logged_id,
                                        p.exerciseId, p.answer)
        } catch (Exception e) {
            //FIXME: log o algo mejor ademas de un mensaje en pantalla.
            logger.error("[ExerciseController] Error al realizar intento de ejercicio; error: ${e}")
        }
        redirect(controller: 'home', action: 'index')
    }
}
