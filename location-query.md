Location Query
--------------

The location butler server operates best when receiving a continuous
stream of location queries. The buddycloud client sends one every time
the active cell changes OR every 3 minutes, whichever happens first.
Single query snapshotting also works.

The buddycloud location server is compliant to
[XEP-0255](http://xmpp.org/extensions/xep-0255.html). See this for
further information and more examples.

Important Note: The buddycloud location server is designed to work with
bookmarked places. That is, assuming you provide it with a continuous
stream of location queries as described above, you can use the [Place
Management](Place Management "wikilink") stanzas to set a bookmark a
place at your current location, giving it whatever name you feel
appropriate. Whenever you return to this place, the location server will
recognize the pattern of cell towers and wifi access points that your
phone reports, and set your current location to this place. This is a
great way to provide personalized location information to your followers
at a level of privacy of your choosing.

Send a simple location query
----------------------------

Client to server:

~~~~ {.xml}
<iq from='you@example.org' 
    to='butler.buddycloud.com' 
    id='023FE3AA3'
    type='get' 
    xml:lang='en-US'>
   <locationquery xmlns='urn:xmpp:locationquery:0'>
      <reference>
         <id>262:07:51047:50880</id>
         <type>cell</type>
         <signalstrength>-79</signalstrength>
      </reference>
   </locationquery>
</iq>
~~~~

Server to client (no place reference found):

~~~~ {.xml}
<iq from='butler.buddycloud.com' 
    id='023FE3AA3' 
    to='you@example.org' 
    type='result' 
    xml:lang='en-US'>
  <geoloc xmlns='http://jabber.org/protocol/geoloc' xml:lang='en'>
    <timestamp>2009-06-22T09:56:05Z</timestamp>
    <lat>48.130733</lat>
    <lon>11.60634</lon>
    <accuracy>400.0</accuracy>
    <text>Haidhausen</text>
    <area>Haidhausen</area>
    <locality>Munich</locality>
    <region>Bavaria</region>
    <country>Germany</country>
  </geoloc>
</iq>
~~~~

Send a location query, full example with GPS coords and publish to
geoloc

Client to server:

~~~~ {.xml}
<iq from='you@example.org' 
    to='butler.buddycloud.com' 
    id='023FE3AA4'
    type='get' 
    xml:lang='en-US'>
   <locationquery xmlns='urn:xmpp:locationquery:0'>
      <lat>48.130605</lat>
      <lon>11.607284</lon>
      <accuracy>35.6</accuracy>
      <publish>true</publish>
      <reference>
         <id>262:07:51047:50880</id>
         <type>cell</type>
         <signalstrength>-79</signalstrength>
      </reference>
      <reference>
         <id>00:1C:4A:D3:53:F8</id>
         <type>wifi</type>
         <signalstrength>-70</signalstrength>
      </reference>
      <reference>
         <id>00:1B:2F:A6:A4:F2</id>
         <type>wifi</type>
         <signalstrength>-65</signalstrength>
      </reference>
      <reference>
         <id>00:09:5B:E8:7A:DA</id>
         <type>wifi</type>
         <signalstrength>-93</signalstrength>
      </reference>
      <reference>
         <id>00:21:FE:90:AA:28</id>
         <type>bluetooth</type>
      </reference>
   </locationquery>
</iq>
~~~~

Server to client (empty result as acknowledge):

~~~~ {.xml}
<iq from='you@example.org' 
    to='butler.buddycloud.com' 
    id='023FE3AA4'
    type='get' 
    xml:lang='en-US' />
~~~~

Server publishes location result to user's geoloc node, (is pushed to
all user's followers):

~~~~ {.xml}
<iq from='user@buddycloud.com' id='items1' type='result'>
  <pubsub xmlns='http://jabber.org/protocol/pubsub'>
    <items node='http://jabber.org/protocol/geoloc'>
      <item id='4D96A45D84D60'>
        <geoloc xmlns='http://jabber.org/protocol/geoloc' xml:lang='en'>
          <uri>http://buddycloud.com/places/22</uri>
          <text>Lisboa bar</text>
          <lat>48.130733</lat>
          <lon>11.60634</lon>
          <accuracy>35.6</accuracy>
          <area>Haidhausen</area>
          <locality>Munich</locality>
          <region>Bavaria</region>
          <country>Germany</country>
        </geoloc>
      </item>
    </items>
  </pubsub>
</iq>
~~~~

Notes: If a <publish>true</publish> element is specified in the location
query, the location results will be published to the querying user's
[geoloc xep-0080](http://xmpp.org/extensions/xep-0080.html)
[pub-sub](http://xmpp.org/extensions/xep-0060.html) node and a empty
result stanza will be returned. Also his previous location will be
published to his 'geoloc-prev' node, when applicable. Clients need thus
not process the result stanza directly, but can instead process the
pub-sub update. This will have to be implemented anyway to get
followings' locations.

When the buddycloud location server detects that a user is back at a
bookmarked place, it will set the <text> element of the location result
to "Near \$placeName". If after ten minutes the user is still at this
place, the "Near " is dropped and the user is considered to be "at" this
place.

When the buddycloud server detects that each location query contains
different references, it will assume that the user is moving and prefix
the <text> element with "On the road in ".

If latitude and longitude were provided, these are echoed in the
location result, if not they will be derived from supplied location
references. In the latter case the accuracy will be depending on the
motion state, and number and type of references seen of fix (in the
order of hundreds of kilometers if only country is known, and down to
50m if place is known and state is motion stationary)
