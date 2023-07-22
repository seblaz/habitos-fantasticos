package code.topia



import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.Method.POST
import org.slf4j.LoggerFactory
import grails.util.Environment

class ExerciseValidator {
    def logger = LoggerFactory.getLogger(getClass())
    def grailsApplication

    def obtenerRespuesta(String consulta) {
        String apiUrl = grailsApplication.config.grails.profiles[Environment.current.getName()]['api-csm-gpt'].url
        String apiKey = grailsApplication.config.grails.profiles[Environment.current.getName()]['api-csm-gpt'].apiKey
        def http = new HTTPBuilder(apiUrl)
        logger.info("[ExerciseValidator] apiurl: ${apiUrl}")
        logger.info("[ExerciseValidator] apiKey: ${apiKey}")
        http.request(POST, ContentType.JSON) { req ->
            headers['Authorization'] = "Bearer ${apiKey}"
            body = [prompt: consulta, max_tokens: 1, n: 1, stop: null, temperature: 0.1]
            response.success = { resp, json ->
                logger.info("Tuvimos respuesta!")
                logger.info("Respuesta generada: ${json}")
                def respuestaGenerada = json.choices[0].text.strip()
                return respuestaGenerada
            }
            response.failure = { resp ->
                return null
            }
        }
    }

    boolean validateAnswer(String answer, Exercise exercise) {
        String consulta = "Dado el enunciado:\n" + exercise.statement +\
                          "\n" + "Con respuesta al enunciado:\n" + answer +\
                          "\n" + "Se pide que se genere una respuesta con 'Aprobado' o 'Rechazado'." +\
                          "Se debe contestar 'Aprobado' solo si la respuesta es considerada una solucion correcta al enunciado."
        logger.info("[ExerciseValidator] Consulta: ${consulta}")
        String respuesta = obtenerRespuesta(consulta)
        logger.info("[ExerciseValidator] Respuesta: ${respuesta}")
        if (respuesta == null || respuesta.contains("Rechazado")) {
            return false
        } else if (respuesta.contains("Aprobado")) {
            return true
        }
        return false
    }
}
