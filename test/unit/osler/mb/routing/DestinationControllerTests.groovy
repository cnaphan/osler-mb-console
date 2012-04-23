package osler.mb.routing



import org.junit.*
import grails.test.mixin.*

@TestFor(DestinationController)
class DestinationControllerTests {   

    void testIndex() {
        controller.index()
        assert "/destination/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.destinationInstanceList.size() == 3
        assert model.destinationInstanceTotal == 3
    }

    void testCreate() {
       def model = controller.create()

       assert model.destinationInstance != null
    }

}
