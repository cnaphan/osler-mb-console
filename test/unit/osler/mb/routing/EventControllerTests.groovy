package osler.mb.routing



import org.junit.*
import grails.test.mixin.*

@TestFor(EventController)
class EventControllerTests {


    def populateValidParams(params) {
      assert params != null
      // TODO: Populate valid properties like...
      //params["name"] = 'someValidName'
    }

    void testIndex() {
        controller.index()
        assert "/event/list" == response.redirectedUrl
    }

    void testList() {

        def model = controller.list()

        assert model.eventInstanceList.size() > 0
        assert model.eventInstanceTotal > 0
    }

    void testCreate() {
       def model = controller.create()

       assert model.eventInstance != null
    }

}
