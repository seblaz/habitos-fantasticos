package code.topia

import grails.testing.gorm.DomainUnitTest
import spock.lang.Specification

class UserGamificationSpec extends Specification implements DomainUnitTest<UserGamification> {


    BeginnerLevel beginnerLevel = new BeginnerLevel()
    AdvancedLevel advancedLevel = new AdvancedLevel()

    def setup() {
    }

    def cleanup() {
    }

    void "create a UserGamification at BeginnerLevel"() {
        when: "create a new user"
        User user = new User("Alejandro", "Pena", "example@example.com")
        UserGamification usGm = user.initGamification(beginnerLevel)

        then: "the UserGamification is valid"
        assert usGm.validate()
    }


    void "could not create a UserGamification at AdvancedLevel"() {
        when: "create a new user"
        User user = new User("Alejandro", "Pena", "example@example.com")
        UserGamification usGm = user.initGamification(advancedLevel)

        then: "the UserGamification is not valid"
        thrown UserGamificationInvalidLevelException
    }

    void "get level user"() {
        when: "create a new user"
        User user = new User("Alejandro", "Pena", "example@example.com")
        UserGamification usGm = user.initGamification(beginnerLevel)

        then: "the new user is valid"
        assert user.validate()
        and: "the level is BeginnerLevel"
        assert usGm.getUserLevel() == beginnerLevel
    }

    void "add an attempt"() {
        given: "a user"
        User user = new User("Alejandro", "Pena", "example@example.com")
        UserGamification usGm = user.initGamification(beginnerLevel)
        when: "add an attempt"
        Exercise ex1 = beginnerLevel.getExercises().get(0)
        Attempt attempt = user.performAttempt(ex1, "print('Hello World')")
        then: "usergamification has the attempt"
        assert usGm.getAllAttempts().contains(attempt)
    }

    void "add an attempt with invalid exercise level"() {
        given: "a user"
        User user = new User("Alejandro", "Pena", "example@example.com")
        UserGamification usGm = user.initGamification(beginnerLevel)
        when: "add an attempt with invalid exercise level"
        Exercise ex1 = advancedLevel.getExercises().get(0)
        usGm.addAttempt(user.performAttempt(ex1, "print('Hello World')"))
        then: "throws AttemptWithInvalidExerciseLevelException"
        thrown AttemptWithInvalidExerciseLevelException
    }

    void "not repeat an attempt when add one"() {
        given: "a user"
        User user = new User("Alejandro", "Pena", "example@example.com")
        UserGamification usGm = user.initGamification(beginnerLevel)
        when: "add an attempt twice"
        Exercise ex1 = beginnerLevel.getExercises().get(0)
        Attempt attempt = user.performAttempt(ex1, "print('Hello World')")
        usGm.addAttempt(attempt)
        usGm.addAttempt(attempt)
        then: "usergamification has only one attempt"
        assert usGm.getAllAttempts().size() == 1
        
    }

    void "add points to user"() {
        given: "a user"
        User user = new User("Alejandro", "Pena", "example@example.com")
        UserGamification usGm = user.initGamification(beginnerLevel)
        when: "add points to user"
        usGm.addPoints(2)
        then: "user has 10 points"
        assert usGm.getUserPoints() == 2
    }


    void "complete level"() {
        given: "a user"
        User user = new User("Alejandro", "Pena", "example@example.com")
        UserGamification usGm = user.initGamification(beginnerLevel)
        when: "the user complete the level"
        usGm.addPoints(beginnerLevel.points)
        then: "has a complete level"
        assert usGm.completeLevels.contains(beginnerLevel)
        and: "has a new level"
        assert usGm.getUserLevel() instanceof AdvancedLevel
    }

}
