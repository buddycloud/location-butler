Create Place
============

Create new place (minimal stanza)
---------------------------------

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#create'>
       <place>
          <name>Home</name> // name required
                            // shared defaults to false
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#create'>
       <place>
         <id>http://buddycloud.com/places/4864</id>
         <name>Home</name>
         <shared>false</shared>
         <revision>0</revision>
       </place>
    </query>
 </iq>
~~~~

Create new place (maximal stanza)
---------------------------------

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#create'>
       <place>
          <name>Joe's Pizza</name>
          <shared>true</shared>
          <description>Best pizza in town</description>
          <street>334 Santa Monica Boulevard</street>
          <area>Santa Monica</area>
          <city>Los Angeles</city>
          <region>California</region>
          <country>USA</country>
          <postalcode>CA 33774</postalcode>
          <lat>33.990234</lat>
          <lon>-118.461204</lon>
          <site>http://joespizza.com</site>
          <wiki>http://wikipedia.org/Joes_Pizza</wiki>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#create'>
       <place>
          <id>http://buddycloud.com/places/4865</id>
          <name>Joe's Pizza</name>
          <shared>true</shared>
          <description>Best pizza in town</description>
          <street>334 Santa Monica Boulevard</street>
          <area>Santa Monica</area>
          <city>Los Angeles</city>
          <region>California</region>
          <country>USA</country>
          <postalcode>CA 33774</postalcode>
          <lat>33.990234</lat>
          <lon>-118.461204</lon>
          <site>http://joespizza.com</site>
          <wiki>http://wikipedia.org/Joes_Pizza</wiki>
          <revision>0</revision>
       </place>
    </query>
 </iq>
~~~~

Place Editing
=============

Edit Place
----------

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#edit'>
       <place>              
         <id>http://buddycloud.com/places/4864</id>
         <name>My Shack</name>
         <shared>true</shared>
         <street>123 Venic Blvd.</name>
         <area>123 Venice Blvd.</name>
         <city>Los Angeles</city>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#edit'>
       <place>
         <id>http://buddycloud.com/places/4864</id>
         <name>My Shack</name>
         <shared>true</shared>
         <street>123 Venic Blvd.</name>
         <area>123 Venice Blvd.</name>
         <city>Los Angeles</city>
         <revision>1</revision>
      </place>
    </query>
 </iq>
~~~~

Delete Place
------------

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#delete'>
       <place>
          <id>http://buddycloud.com/places/4865</> // id required
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com' />
~~~~

Place Details
=============

Get place details
-----------------

Client to server:

~~~~ {.xml}
<iq type='get' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place'>
       <place>
          <id>http://buddycloud.com/places/4865</id>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place'>
       <place>
          <id>4865</id>
          <name>Joe's Pizza</name>
          <shared>true</shared>
          <description>Best pizza in town</description>
          <street>334 Santa Monica Boulevard</street>
          <area>Santa Monica</area>
          <city>Los Angeles</city>
          <region>California</region>
          <country>USA</country>
          <postalcode>CA 33774</postalcode>
          <lat>33.990234</lat>
          <lon>-118.461204</lon>
          <site>http://joespizza.com</site>
          <wiki>http://wikipedia.org/Joes_Pizza</wiki>
          <revision>1</revision>
       </place>
    </query>
 </iq>
~~~~

Get place details (selected fields)
-----------------------------------

Client to server:

~~~~ {.xml}
<iq type='get' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place'>
       <options>
          <feature var='name' />
          <feature var='lat' />
          <feature var='lon' />
          <feature var='population' />
       </options>
       <place>
          <id>http://buddycloud.com/places/4865</id>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place'>
       <place>
          <name>Joe's Pizza</name>
          <lat>33.990234</lat>
          <lon>-118.461204</lon>
          <population>3</population>
       </place>
    </query>
 </iq>
~~~~

Search for places
-----------------

Search for places in bc and third-party databases

Client to server:

~~~~ {.xml}
<iq type='get' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#search'>
       <place>
          <name>Joe's Pizza</name> // name required, id forbidden
          <city>Los Angeles</city> // all other fields optional, to limit search
          <region>California</region>
          <country>USA</country>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#search'>
       <place>
          <id>http://buddycloud.com/places/4865</id>
          <name>Joe's Pizza</name>
          <shared>true</shared>
          <description>Best pizza in town</description>
          <street>334 Santa Monica Boulevard</street>
          <area>Santa Monica</area>
          <city>Los Angeles</city>
          <region>California</region>
          <country>USA</country>
          <postalcode>CA 33774</postalcode>
          <lat>33.990234</lat>
          <lon>-118.461204</lon>
          <site>http://joespizza.com</site>
          <wiki>http://wikipedia.org/Joes_Pizza</wiki>
          <revision>0</revision>
       </place>
       <place>
          // when id not present, place is suggestion from 3rd party service, not yet created in buddycloud
          <name>Joe's Pizza</name>
          <shared>true</shared>
          <description>2nd best pizza in town</description>
          <street>123 Pacific Road</street>
          <area>Malibu</area>
          <city>Los Angeles</city>
          <region>California</region>
          <country>USA</country>
          <postalcode>CA 33774</postalcode>
          <lat>34.120234</lat>
          <lon>-118.481204</lon>
          <site>http://joespizza.net</site>
          <wiki>http://wikipedia.org/Joes_Pizza_Malibu</wiki>
       </place>
    </query>
 </iq>
~~~~

Set Current
-----------

Check in to place / create placemark (will be used to re-locate user to
place every time he returns)

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#current'>
       <place>
          <id>http://buddycloud.com/places/4865</id>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#current'>
       <place>
          <id>http://buddycloud.com/places/4865</id>
       </place>
    </query>
 </iq>
~~~~

Server publishes place details to user's geoloc node

...

Going Places
============

Set Next
--------

Set next place by id
--------------------

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#next'>
       <place>
          <id>http://buddycloud.com/places/4865</id>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#next'>
       <place>
          <id>http://buddycloud.com/places/4865</id>
       </place>
    </query>
 </iq>
~~~~

Server publishes place details to users geoloc node

...

Set next place by name
----------------------

NOTE: A new place will never be created when setting next place. The
name submitted will be treated as a one-time place, and can thus be used
very informally. E.g. "Some restaurant", "New York Tomorrow", "The Pub @
8pm" are all valid next place names

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#next'>
       <place>
          <name>Joe's Pizza @ 5pm</name>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com' />
~~~~

Server publishes place details to users geoloc node

...

Place bookmarks
===============

A user will only be auto-located to places they have previously
bookmarked.

Add place to place bookmark list
--------------------------------

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#add'>
       <place>
          <id>http://buddycloud.com/places/123</id>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#add'>
       <place>
          <id>http://buddycloud.com/places/123</id>
       </place>
    </query>
 </iq>
~~~~

Remove place from bookmark list
-------------------------------

Client to server:

~~~~ {.xml}
<iq type='set' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#remove'>
       <place>
          <id>http://buddycloud.com/places/123</id>
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#remove'>
       <place>
          <id>http://buddycloud.com/places/123</id>
       </place>
    </query>
 </iq>
~~~~

Get place bookmarks
-------------------

Client to server:

~~~~ {.xml}
<iq type='get' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#myplaces'>
       <options>
          <feature var='id' />
          <feature var='name' />
       </options>
    </query>
 </iq>
~~~~

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns="http://buddycloud.com/protocol/place#myplaces">
       <place>
          <id>http://buddycloud.com/places/735</id>
          <name>Home</name>
       </place>
       <place>
          <id>http://buddycloud.com/places/746</id>
          <name>Office</name>
       </place>
       <place>
          <id>http://buddycloud.com/places/899</id>
          <name>The Pub</name>
       </place>
       <place>
          <id>http://buddycloud.com/places/916</id>
          <name>Bob'sPlace</name>
       </place>
    </query>
 </iq>
~~~~

Place History
=============

Get place history
-----------------

Client to server:

~~~~ {.xml}
<iq type='get' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#history'>
       <options max='5' tmin='2009-03-29T00:00:00Z' tmax='2009-04-01T00:00:00Z'>
          <feature var='id' />
       </options>
    </query>
 </iq>
~~~~

~~~~ {.xml}
<iq type='result' from='butler.buddycloud.com'>
    <query xmlns="http://buddycloud.com/protocol/place#history">
       <item sequence='0' entered='2009-03-30T13:56:05Z'>
          <place>
             <id>http://buddycloud.com/places/4864</id>
          </place>
       </item>
       <item sequence='-1' entered='2009-03-30T08:56:05Z' left='2009-03-30T10:32:07Z'>
          <place>
             <id>http://buddycloud.com/places/4011</id>
          </place>
       </item>
       <item sequence='-2' entered='2009-03-29T20:12:43Z' left='2009-03-29T23:42:44Z'>
          <place>
             <id>http://buddycloud.com/places/3267</id>
          </place>
       </item>
       <item sequence='-3' entered='2009-03-29T14:33:33Z' left='2009-03-29T19:44:23Z'>
          <place>
             <id>http://buddycloud.com/places/2290</id>
          </place>
       </item>
       <item sequence='-4' entered='2009-03-29T07:16:56Z' left='2009-03-29T08:34:01Z'>
          <place>
             <id>http://buddycloud.com/places/4010</id>
          </place>
       </item>
    </query>
 </iq>
~~~~

Place Visitors
==============

(currently unsupported in the current version on
<https://github.com/buddycloud/location-butler>)

People who have been here
-------------------------

People who have visited this place in the past (from place history).
Should include people currently there?

Client to server:

~~~~ {.xml}
<iq type='get' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#visitors' node='past'> // Or 'history'?
       <place>
          <id>http://buddycloud.com/places/1198</id> // id required
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='maitred.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/channels#visitors' node='past'>
       <user>
          <jid>simon@buddycloud.com</jid> 
       </user>
       <user>
          <jid>elvis@buddycloud.com</jid> 
       </user>
       <user>
          <jid>helium@buddycloud.com</jid> 
       </user>
    </query>
 </iq>
~~~~

People who are going here
-------------------------

People who plan on visiting this place in the future (from next place).

Client to server:

~~~~ {.xml}
<iq type='get' to='butler.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/place#visitors' node='next'>
       <place>
          <id>http://buddycloud.com/places/5130</id> // id required
       </place>
    </query>
 </iq>
~~~~

Server to client:

~~~~ {.xml}
<iq type='result' from='maitred.buddycloud.com'>
    <query xmlns='http://buddycloud.com/protocol/channels#visitors' node='next'>
       <user>
          <jid>elvis@buddycloud.com</jid> 
       </user>
    </query>
 </iq>
~~~~
