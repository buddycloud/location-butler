The location butler provides XEP-0255 compliant location service. It's designed for use by buddycloud clients.

The location butler will describe your location as:
	previous: at home
	currently: on the road in SoHo, London (it detects you are moving)
	future location: Dean Street Coffee Bar (currently manually added althrough a prediction engine would be nice)
Naturally when you arrive at your location the server will remove the future location setting and update your current location.

The buddycloud location butler is responsible for handling location-related
requests from the clients and delegating to dedicated handlers:
 - Beacon logs are added to the database.
 - Every time a user has logged a beacon, the butler triggers a scan through the logged
   history of that user, looking for a stable pattern of beacons, indicating that
   the user is stationary. If so, it looks in the table of defined locations to find
   a match for the beacon pattern. If this succeeds, the location presence of the 
   user is updated. If a location matching the pattern cannot be found, a request is 
   sent to the user to name his current whereabouts. If he/she chooses to do so, a new
   location is added to the locations table including the user provided name and 
   the pattern he just logged. The user may also choose to not name the location.

What is a place bookmark?
Bookmarking a place is sometimes called checking-in on other services. Buddycloud's location butler aims to be 100% 
check-in free. Bookmark a place once, and every time you return there, you are automatically placed back.

Your phone's GSM radio jumps around between GSM towers. Ignore the signal strenth (this varies too much to be useful). 
A bookmark using cell towers could be described as followers:
	20% of a given time window attached to tower with a GSM-ID: 1:2:3
	70% of a given time window attached to tower with a GSM-ID: 2:3:4
	10% of a given time window attached to tower with a GSM-ID: 3:4:5
	=PlaceID(31)
	="Home Sweet Home"
	
In practice, looking at the tower IDs over a 20 minute time window gives a nice approximation of place.

WiFi-based bookmarking works in a similar way: 
The phone reports the WiFi MAC addresses that it sees and the location butler will look them up in it's database of patterns and match something like:
	aa:bb:cc:dd:ee:ff
	bb:cc:dd:ee:ff:aa
	cc:dd:ee:ff:aa:bb
	=placeID(32)
	="My favourite coffee shop"

GPS based beacons (lat/long) are easier. Just snap the user when they are nearby a place.
	
The server works as follows:

1. mobile or web clients send beacon logs every 2 or 3 minutes.
2. the server looks at the jid sending the beacon
	2a. if there is no known history for that jid, start building up history of the beacons seen by that jid in the last 20 minutes.
	2b. check whether the beacon that just arrived is included in any of the known patterns is already a known pattern of beacons for that jid.
3. compare the incoming beacons with known patterns (bookmarks) of that jid.
4. return a probabilty back to the client.
5. if it looks like the client is no longer at a placeID, update their location to "near" placeID.
6. if it looks like a client is not near a placeID, change their current locaiton to just be their broad location. e.g. "SoHo London, UK"

The server also handles place bookmarking according to the specs: http://buddycloud.com/cms/node/103

To start the buddycloud location butler:
java -Duser.timezone=UTC \
-cp .:com.bcloud.server.jar:\
../lib/json.org:\
../lib/log5j-1.2.jar:\
../lib/jip-profile.jar:\
../lib/jso-full.jar:\
../lib/postgresql-8.3-604.jdbc3.jar \
com.buddycloud.location.xmpp.LocationButler
