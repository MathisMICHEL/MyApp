mobilenet_V2 is stored in 'assets/mobilenet_v2.tflite'
It is downloaded using 'To_Tflite.py' script



The different sensors used are luminosity, accelerometer sensors and camera


UI
Luminosity and accelerometer data are updating in real time and displayed using 2D graphs
Preview view of the camera
An authentication button that allow user to authenticate/create account 
A capture button that allow user to take picture


Firebase: 3 services are used by the cloud: 

*Cloud Firestore (noSQL) handle two databases:
    {entity,id} this database is composed of entities related to the id attributed by mobileV2net
    {email,predictedClassIndex,timestamp}   Stores the mail of the user,the timestamp and the 
                        entity's id predicted by the model, once a picture is taken

*Authentication
Firebase store the email address, date of creation, date of last connection and UUID of each account

*Functions
One function is used to import {entity, id}; importEntityData from 
'https://github.com/leferrad/tensorflow-mobilenet/blob/master/imagenet/labels.json'



Once a picture is taken by the user, it is saved inside its phone stockage
    'context.getExternalFilesDir(null), "Pictures/myAppImages"'
The picture is processed into TensorFlow-friendly data, which analyze and return an id 
    corresponding to the entity identified in the picture (if the tensorflow model works)
The name of the entity is fetch back and returned to the user by a Toast

