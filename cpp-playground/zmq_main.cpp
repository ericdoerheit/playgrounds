#include <iostream>
#include <zmq.hpp>

struct Object_t{
    uint32_t id;
    uint32_t label;
    float x;
    float y;
    float z;
    float w;
    float h;
    float d;
    float roll;
    float pitch;
    float yaw;

    Object_t() {}

    Object_t(uint32_t id, uint32_t label, float x, float y, float z, float w, float h, float d, float roll, float pitch,
             float yaw) : id(id), label(label), x(x), y(y), z(z), w(w), h(h), d(d), roll(roll), pitch(pitch),
                          yaw(yaw) {}
};

struct ObjectMessage_t {
    uint32_t number_objects;
    int64_t timestamp;
    Object_t objects[];
};

int main() {a

    bool run = true;

    std::cout << sizeof(uint32_t) << " " << sizeof(int) << " " << sizeof(uint64_t) << " " << sizeof(long) << std::endl;
    std::cout << sizeof(ObjectMessage_t) << std::endl;
    std::cout << sizeof(Object_t*) << std::endl;

    zmq::context_t context (1);
    zmq::socket_t socket (context, ZMQ_STREAM);
    socket.bind ("tcp://*:5555");

    uint8_t id [256];
    size_t id_size = 256;

//        socket.recv(id, 256, 0);

    int counter = 0;

    while (run) {

        //  Wait for next request from client
        zmq::message_t idMessage;
        socket.recv(&idMessage);
        std::cout << "ID message size: " << idMessage.size() << std::endl;


//        std::string data((char*)request.data(), request.size());

        if(counter >= 0) {
            zmq::message_t message;
            socket.recv(&message);

            std::cout << "BBox message size: " << idMessage.size() << std::endl;
            ObjectMessage_t* objectMessage = (ObjectMessage_t*) message.data();
            std::cout << "Number of objects: " << objectMessage->number_objects << std::endl;
            std::cout << "id: " << objectMessage->objects[0].id << " label: " << objectMessage->objects[0].label << std::endl;

            if(objectMessage->number_objects != 10) continue;
            for (int i = 0; i < objectMessage->number_objects; ++i) {
                std::cout << "id: " << objectMessage->objects[i].id << " label: " << objectMessage->objects[i].label
                          << std::endl;
            }
        }

        ++counter;

        //  Send reply back to client
//        zmq::message_t reply (1);
//        memcpy(reply.data (), (void*) B, 1);
//        socket.send(reply);
    }

    return 0;
}
