AndroidCentre
=============

a platform build connections between android devices

Intent is the basic IPC in Android, and Intent is Parcelable, which means Intent over internet is possible and easy.
With so many things trys to be smart as a smartphone, the best way to do this maybe is just make things connected to
a smartphone, and let the phone do the smart thing, simple easy and everyone happy.

Here is an example, the navigation in a car is, to be honest, really stupid. There is no update on data about half a year,
there is no real time data unless a sim card is inserted only to get internet accessability, and there is no convenient way to 
install a new and better map. On the other hand, when a car can be connected to a phone, a really ordinary one, everything changes.
All the problems gone, and the car can be significantly cheaper.

So I think the best smart everything is just a peripheral of an android phone. To make this possible, I imagine the following
steps.

For a peripheral P, and an android phone A.
A keeps a background service running, listening to a port.
P keeps the APK of newest phone side app, which will do all the complex calculation and internet connections.

When P connects to A, P pushes APK to A as a server side plugin.

The connections is established by this platform, each Intent operation supported by android will be supported. 

The platform is based on json-rpc2, built follow these steps:
  * build a simple json-rpc server on android, establishing http connections.
  * passing parcelable over json, only using json is quite costly, should use parcelable as much as possible
  * making start activity、service、broadcast possible
