<h2>Trip outline</h2>
<p>Kirk has kindly agreed to share his journey to the office. Kirk has an interesting commute through Wales.</p>

<p>Kirk sets out from "Home 2.0", picks up some friends at the station, picks up some food at Tesco and ends up at somewhere he's bookmarked called "Office" (which looks like an aircraft hangar if you zoom in).</p>
<h2>Route</h2>
[[File:LocationAPIexample.png|Route taken with the different locations highlighted]]
([http://maps.google.com/maps?f=d&source=s_d&saddr=9+Howell+Road+Ely+Cardiff+CF5+4HY&daddr=St+Fagans+level+crossing+to:Radyr+Train+station+to:CF31+3,+Bridgend,+UK+(Abbey+National+Plc+(Tesco))+to:Llandow,+Cowbridge,+The+Vale+of+Glamorgan,+CF71,+UK&geocode=%3B%3BFVwUEgMdd3DO_yETgmI7AhvTcQ%3BFc3KEQMd04vJ_yEgSMm2F7k6Yg%3B&hl=en&mra=ls&doflg=ptk&sll=51.487262,-3.263884&sspn=0.024371,0.077248&ie=UTF8&ll=51.482452,-3.403702&spn=0.131918,0.42984&z=12 interactive map])

The key points on the map are:
<ul>
	<li>A: Home 2.0</li>
	<li>B: St Fagans level crossing</li>
	<li>C: Radyr station car park (to pick up some people)</li>
	<li>D: Tescos store (bookmarked when he was there)</li>
	<li>E: Office</li>
</ul>

<h2>Summary of key location server events</h2>
<ul>
	<li>Synchronising time: Buddycloud does a bluetooth scan every 5 minutes to see who else is nearby. This time-sync is used to calculate the offset between the phone's time and an agreed-upon time source.</li>
	<li>Getting a place list: Kirk's mobile client downloads a list of all his known places. These are places that he wishes to be placed at.</li>
	<li>Nearby query: Kirk checks for other Buddycloud bookmarked places nearby</li>
	<li>Beacon log with GPS: The buddycloud client logs with GPS</li>
	<li>Beacon log without GPS: The buddycloud client logs using just cell-tower identifiers</li>
		<li>Setting his intended place: Kirk sets where he plans to go. In this example he shows that he's going to the pub later.</li>

	<li>Bookmarking a new place: Kirk bookmarks Tescos. Next time he is back there, he will be displayed there.</li>
	<li>Arriving at the office: Kirk arrives at work. The longer that he is there, the more certain the server is of his location</li>
</ul>

<h2>Summary of location queries</h2>
This is a list of all the beacon queries his phone submitted to the location server on the way and how it replied. Remember that these labels are also being published to his friends. Each change in label triggers an update to his <a href="http://xmpp.org/extensions/xep-0080.html">geoloc</a> node.  For simplicity this XMPP trace doesn't show any pub-sub activity. 

<syntaxhighlight lang="XML"> cellpatternquality="11"  label="Near Home 2.0" placeid="4800" state="restless"
cellpatternquality="28"  label="Near Home 2.0" placeid="4800" state="restless"
cellpatternquality="40"  label="Near Home 2.0" placeid="4800" state="restless"
cellpatternquality="40"  label="Ely" placeid="0" state="restless"
cellpatternquality="00"  label="Ely" placeid="0" state="restless"
cellpatternquality="00"  label="On the road in Saint Fagans" placeid="0" state="moving"
cellpatternquality="00"  label="Saint Fagans" placeid="0" state="restless"
cellpatternquality="33"  label="Saint Fagans" placeid="0" state="restless"
cellpatternquality="34"  label="Saint Fagans" placeid="0" state="restless"
cellpatternquality="45"  label="Saint Fagans" placeid="0" state="restless"
cellpatternquality="53"  label="Saint Fagans" placeid="0" state="restless"
cellpatternquality="34"  label="On the road in Radyr" placeid="0" state="moving"
cellpatternquality="30"  label="On the road in Radyr" placeid="0" state="moving"
cellpatternquality="53"  label="Radyr" placeid="0" state="restless"
cellpatternquality="41"  label="Radyr" placeid="0" state="restless"
cellpatternquality="41"  label="Llandaff" placeid="0" state="restless"
cellpatternquality="54"  label="Llandaff" placeid="0" state="restless"
cellpatternquality="54"  label="On the road in Llandaff" placeid="0" state="moving"
cellpatternquality="36"  label="Llandaff" placeid="0" state="restless"
cellpatternquality="67"  label="Llandaff" placeid="0" state="restless"
cellpatternquality="100" label="Llandaff" placeid="0" state="restless"
cellpatternquality="98"  label="On the road in Pentyrch" placeid="0" state="moving"
cellpatternquality="64"  label="On the road in Hensol" placeid="0" state="moving"
cellpatternquality="66"  label="On the road in Hensol" placeid="0" state="moving"
cellpatternquality="36"  label="On the road in Hensol" placeid="0" state="moving"   
cellpatternquality="61"  label="On the road in Llanharry" placeid="0" state="moving"
cellpatternquality="63"  label="On the road in Llanharry" placeid="0" state="moving"
cellpatternquality="0"   label="On the road in Llanharan" placeid="0" state="moving"
cellpatternquality="0"   label="On the road in Pencoed" placeid="0" state="moving"  
cellpatternquality="0"   label="On the road in Coychurch" placeid="0" state="moving"
cellpatternquality="0"   label="On the road in Treoes" placeid="0" state="moving"
cellpatternquality="0"   label="On the road in Ewenny" placeid="0" state="moving" 
cellpatternquality="36"  label="On the road in Ewenny" placeid="0" state="moving"
cellpatternquality="62"  label="On the road in Ewenny" placeid="0" state="moving"
cellpatternquality="88"  label="Ewenny" placeid="0" state="restless"
cellpatternquality="100" label="Ewenny" placeid="0" state="restless"
cellpatternquality="11"  label="Near Tesco bridgend" placeid="4874" state="restless"
cellpatternquality="100" label="On the road in Ewenny" placeid="0" state="moving"
cellpatternquality="100" label="On the road in Treoes" placeid="0" state="moving"
cellpatternquality="100" label="On the road in Colwinston" placeid="0" state="moving"
cellpatternquality="0"   label="On the road in Llangan" placeid="0" state="moving"  
cellpatternquality="100" label="On the road in Llangan" placeid="0" state="moving"  
cellpatternquality="22"  label="On the road in Llysworney" placeid="0" state="moving"
cellpatternquality="35"  label="On the road in Llysworney" placeid="0" state="moving"
cellpatternquality="24"  label="On the road in Llysworney" placeid="0" state="moving"
cellpatternquality="11"  label="Llysworney" placeid="0" state="restless"
cellpatternquality="11"  label="On the road in Llysworney" placeid="0" state="moving"
cellpatternquality="88"  label="On the road in Llysworney" placeid="0" state="moving"
cellpatternquality="100" label="Near Office" placeid="2195" state="restless"
cellpatternquality="100" label="Office" placeid="2195" state="stationary"
cellpatternquality="11"  label="Office" placeid="2195" state="stationary" </syntaxhighlight>

<h2>Raw location query output</h2>
<H3>Synchronising time</H3>
<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T08:11:08</utc>
	</query>
</iq>
</syntaxhighlight>

<H3>Getting a list of Kirk's places</H3>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="subscriptions1">
	<command xmlns="http://jabber.org/protocol/commands" node="place">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:place:subscriptions</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="subscriptions1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
			<item>
			<field type="text-single" var="name">
				<value>Office</value>
			</field>
			<field type="text-single" var="id">
				<value>2195</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Travelling</value>
			</field>
			<field type="text-single" var="id">
				<value>2238</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Home 1.0</value>
			</field>
			<field type="text-single" var="id">
				<value>2477</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Radyr court inn</value>
			</field>
			<field type="text-single" var="id">
				<value>2613</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Radyr station car park</value>
			</field>
			<field type="text-single" var="id">
				<value>4256</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Home 2.0</value>
			</field>
			<field type="text-single" var="id">
				<value>4800</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:09:56Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5360:39422</id>
			<type>cell</type>
			<signalstrength>88</signalstrength>
		</reference>
		<reference>
			<id>00:1B:2F:E7:69:0E</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>57</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Ely" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>
<H3>Checking for nearby places</H3>
<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
			<item>
			<field type="text-single" var="name">
				<value>Home 2.0</value>
			</field>
			<field type="text-single" var="id">
				<value>4800</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>
<H3>Sending a beacon log with GPS</H3>
<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:10:50Z</timestamp>
		<lat>51.479588</lat>
		<lon>-3.252332</lon>
		<accuracy>26.87</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5360:39422</id>
			<type>cell</type>
			<signalstrength>91</signalstrength>
		</reference>
		<reference>
			<id>00:1B:2F:E7:69:0E</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>66</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Near Home 2.0" placeid="4800" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T08:13:39</utc>
	</query>
</iq>
</syntaxhighlight>
<H3>Sending beacon logs using just wifi and cell-tower data</H3>
<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:12:25Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5360:39561</id>
			<type>cell</type>
			<signalstrength>83</signalstrength>
		</reference>
		<reference>
			<id>00:1B:2F:E7:69:0E</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>58</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="28" label="Near Home 2.0" placeid="4800" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:14:40Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5360:39422</id>
			<type>cell</type>
			<signalstrength>97</signalstrength>
		</reference>
		<reference>
			<id>00:1B:2F:E7:69:0E</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>65</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="28" label="Near Home 2.0" placeid="4800" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:16:07Z</timestamp>
		<lat>51.479453</lat>
		<lon>-3.252402</lon>
		<accuracy>125.61</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4882699</id>
			<type>cell</type>
			<signalstrength>93</signalstrength>
		</reference>
		<reference>
			<id>00:17:3F:3D:47:22</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>99</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:EA:9C:A0</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>83</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="40" label="Near Home 2.0" placeid="4800" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:18:51Z</timestamp>
		<lat>51.477367</lat>
		<lon>-3.254560</lon>
		<accuracy>56.64</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4881797</id>
			<type>cell</type>
			<signalstrength>92</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="40" label="Ely" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:19:52Z</timestamp>
		<lat>51.474977</lat>
		<lon>-3.263378</lon>
		<accuracy>91.18</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4863363</id>
			<type>cell</type>
			<signalstrength>90</signalstrength>
		</reference>
		<reference>
			<id>00:1E:E5:5A:D2:4E</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>93</signalstrength>
		</reference>
		<reference>
			<id>00:17:3F:E4:72:B1</id>
			<type>wifi</type>
			<enc>open</enc>
			<signalstrength>89</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="Ely" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:21:54Z</timestamp>
		<lat>51.479501</lat>
		<lon>-3.270546</lon>
		<accuracy>70.07</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4872110</id>
			<type>cell</type>
			<signalstrength>109</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Saint Fagans" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:22:11Z</timestamp>
		<lat>51.479443</lat>
		<lon>-3.270748</lon>
		<accuracy>56.47</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5560:21763</id>
			<type>cell</type>
			<signalstrength>109</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="Saint Fagans" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:23:38Z</timestamp>
		<lat>51.483670</lat>
		<lon>-3.269686</lon>
		<accuracy>55.09</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:20709</id>
			<type>cell</type>
			<signalstrength>100</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="Saint Fagans" placeid="0" state="restless"/>
</iq>
</syntaxhighlight> 

<h3>Setting his next location</h3>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="set" id="next1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:set_next</value>
			</field>
			<field type="text-single" var="label">
				<value>Radyr court inn</value>
			</field>
			<field type="text-single" var="place_id">
				<value>2613</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="next1" type="result" xml:lang="en-GB"/>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="set" id="next1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:set_next</value>
			</field>
			<field type="text-single" var="label">
				<value>Radyr station car park</value>
			</field>
			<field type="text-single" var="place_id">
				<value>4256</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="next1" type="result" xml:lang="en-GB"/>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="kbateman@buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="mood1" type="result"/>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:26:40Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:20709</id>
			<type>cell</type>
			<signalstrength>87</signalstrength>
		</reference>
		<reference>
			<id>00:18:4D:C9:47:18</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>88</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="33" label="Saint Fagans" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:26:55Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5360:38611</id>
			<type>cell</type>
			<signalstrength>92</signalstrength>
		</reference>
		<reference>
			<id>00:18:4D:C9:47:18</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>88</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:26:58Z</timestamp>
		<lat>51.488277</lat>
		<lon>-3.269161</lon>
		<accuracy>134.34</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5560:21763</id>
			<type>cell</type>
			<signalstrength>98</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="34" label="Saint Fagans" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="45" label="Saint Fagans" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:28:02Z</timestamp>
		<lat>51.495680</lat>
		<lon>-3.272686</lon>
		<accuracy>61.67</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5360:39913</id>
			<type>cell</type>
			<signalstrength>81</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="53" label="Saint Fagans" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:29:06Z</timestamp>
		<lat>51.505615</lat>
		<lon>-3.278303</lon>
		<accuracy>64.38</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5560:21763</id>
			<type>cell</type>
			<signalstrength>59</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:29:11Z</timestamp>
		<lat>51.506190</lat>
		<lon>-3.278174</lon>
		<accuracy>98.72</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4872110</id>
			<type>cell</type>
			<signalstrength>91</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="34" label="On the road in Radyr" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="34" label="On the road in Radyr" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:31:29Z</timestamp>
		<lat>51.509764</lat>
		<lon>-3.260162</lon>
		<accuracy>177.96</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4880786</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="30" label="On the road in Radyr" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:34:38Z</timestamp>
		<lat>51.506063</lat>
		<lon>-3.252467</lon>
		<accuracy>112.38</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4880786</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="set" id="next2">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:set_next</value>
			</field>
			<field type="text-single" var="label">
				<value/>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="next2" type="result" xml:lang="en-GB"/>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="53" label="Radyr" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:35:34Z</timestamp>
		<lat>51.509000</lat>
		<lon>-3.250146</lon>
		<accuracy>44.63</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4871716</id>
			<type>cell</type>
			<signalstrength>102</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="41" label="Radyr" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:36:21Z</timestamp>
		<lat>51.511209</lat>
		<lon>-3.250688</lon>
		<accuracy>29.63</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4871722</id>
			<type>cell</type>
			<signalstrength>102</signalstrength>
		</reference>
		<reference>
			<id>00:1F:33:03:3B:80</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>87</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="41" label="Llandaff" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:36:29Z</timestamp>
		<lat>51.511812</lat>
		<lon>-3.250634</lon>
		<accuracy>76.41</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4871716</id>
			<type>cell</type>
			<signalstrength>103</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="41" label="Llandaff" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:37:29Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4882658</id>
			<type>cell</type>
			<signalstrength>112</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:37:29Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4861733</id>
			<type>cell</type>
			<signalstrength>105</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="54" label="Llandaff" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="54" label="Llandaff" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:39:09Z</timestamp>
		<lat>51.514657</lat>
		<lon>-3.249886</lon>
		<accuracy>90.38</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4882658</id>
			<type>cell</type>
			<signalstrength>108</signalstrength>
		</reference>
		<reference>
			<id>00:16:B6:B7:9F:8C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>89</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="54" label="On the road in Llandaff" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:40:57Z</timestamp>
		<lat>51.506816</lat>
		<lon>-3.250232</lon>
		<accuracy>81.68</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4871716</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:41:00Z</timestamp>
		<lat>51.506608</lat>
		<lon>-3.250566</lon>
		<accuracy>119.67</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4880786</id>
			<type>cell</type>
			<signalstrength>98</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="36" label="Llandaff" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="67" label="Llandaff" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:42:55Z</timestamp>
		<lat>51.511433</lat>
		<lon>-3.274606</lon>
		<accuracy>127.36</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4872110</id>
			<type>cell</type>
			<signalstrength>102</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="Llandaff" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:44:42Z</timestamp>
		<lat>51.512356</lat>
		<lon>-3.309483</lon>
		<accuracy>34.18</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4882110</id>
			<type>cell</type>
			<signalstrength>95</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T08:45:58</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="98" label="On the road in Pentyrch" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:47:55Z</timestamp>
		<lat>51.520431</lat>
		<lon>-3.353436</lon>
		<accuracy>73.25</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4882110</id>
			<type>cell</type>
			<signalstrength>98</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="64" label="On the road in Hensol" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:48:15Z</timestamp>
		<lat>51.520440</lat>
		<lon>-3.353733</lon>
		<accuracy>102.21</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4938791</id>
			<type>cell</type>
			<signalstrength>85</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="66" label="On the road in Hensol" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq type="get" id="purple4cd26a29" to="kbateman@buddycloud.com/buddycloud">
	<query xmlns="jabber:iq:last"/>
</iq>
</syntaxhighlight>
<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:51:22Z</timestamp>
		<lat>51.518565</lat>
		<lon>-3.359023</lon>
		<accuracy>77.74</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4882110</id>
			<type>cell</type>
			<signalstrength>111</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="36" label="On the road in Hensol" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T08:54:41</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:54:34Z</timestamp>
		<lat>51.514953</lat>
		<lon>-3.405103</lon>
		<accuracy>31.52</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35175:4882110</id>
			<type>cell</type>
			<signalstrength>108</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="61" label="On the road in Llanharry" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:55:00Z</timestamp>
		<lat>51.513184</lat>
		<lon>-3.414587</lon>
		<accuracy>62.32</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5560:26466</id>
			<type>cell</type>
			<signalstrength>113</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="63" label="On the road in Llanharry" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:55:15Z</timestamp>
		<lat>51.511892</lat>
		<lon>-3.420721</lon>
		<accuracy>24.11</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5560:31747</id>
			<type>cell</type>
			<signalstrength>82</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="63" label="On the road in Llanharry" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:56:05Z</timestamp>
		<lat>51.510450</lat>
		<lon>-3.445882</lon>
		<accuracy>70.22</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:10142</id>
			<type>cell</type>
			<signalstrength>91</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Llanharan" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:56:15Z</timestamp>
		<lat>51.510993</lat>
		<lon>-3.450272</lon>
		<accuracy>52.63</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:11746</id>
			<type>cell</type>
			<signalstrength>73</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Llanharan" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:57:49Z</timestamp>
		<lat>51.512527</lat>
		<lon>-3.496015</lon>
		<accuracy>69.30</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:17166</id>
			<type>cell</type>
			<signalstrength>83</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Pencoed" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:59:06Z</timestamp>
		<lat>51.510323</lat>
		<lon>-3.516393</lon>
		<accuracy>54.56</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:10429</id>
			<type>cell</type>
			<signalstrength>84</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T08:59:14Z</timestamp>
		<lat>51.509772</lat>
		<lon>-3.518983</lon>
		<accuracy>53.99</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4927335</id>
			<type>cell</type>
			<signalstrength>103</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Coychurch" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Coychurch" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:01:46Z</timestamp>
		<lat>51.496514</lat>
		<lon>-3.547605</lon>
		<accuracy>115.69</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937335</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:30:4F:3F:38:60</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>88</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Treoes" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:01:50Z</timestamp>
		<lat>51.496378</lat>
		<lon>-3.548950</lon>
		<accuracy>105.62</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937387</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Ewenny" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:02:57Z</timestamp>
		<lat>51.496468</lat>
		<lon>-3.562694</lon>
		<accuracy>112.56</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4947387</id>
			<type>cell</type>
			<signalstrength>93</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Ewenny" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:06:27Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4947387</id>
			<type>cell</type>
			<signalstrength>102</signalstrength>
		</reference>
		<reference>
			<id>00:0B:FD:F3:A5:12</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>75</signalstrength>
		</reference>
		<reference>
			<id>00:0F:F8:58:E2:19</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>85</signalstrength>
		</reference>
		<reference>
			<id>00:0F:F8:58:E2:15</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>89</signalstrength>
		</reference>
		<reference>
			<id>00:07:50:D5:B1:D5</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>61</signalstrength>
		</reference>
		<reference>
			<id>00:07:50:D5:B1:C6</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>62</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="36" label="On the road in Ewenny" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:09:57Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4947387</id>
			<type>cell</type>
			<signalstrength>92</signalstrength>
		</reference>
		<reference>
			<id>00:0F:F8:58:E2:19</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>78</signalstrength>
		</reference>
		<reference>
			<id>00:0B:FD:F3:A5:12</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>64</signalstrength>
		</reference>
		<reference>
			<id>00:0B:FD:F3:A5:05</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>82</signalstrength>
		</reference>
		<reference>
			<id>00:0B:FD:F3:A4:41</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>87</signalstrength>
		</reference>
		<reference>
			<id>00:0F:F8:58:E2:02</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>84</signalstrength>
		</reference>
		<reference>
			<id>00:0F:F8:58:E2:15</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>71</signalstrength>
		</reference>
		<reference>
			<id>00:07:50:D6:57:E4</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>71</signalstrength>
		</reference>
		<reference>
			<id>00:07:50:D5:B1:C6</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>66</signalstrength>
		</reference>
		<reference>
			<id>00:07:50:D5:B1:D5</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>86</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="62" label="On the road in Ewenny" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T09:13:44</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:13:27Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4947387</id>
			<type>cell</type>
			<signalstrength>89</signalstrength>
		</reference>
		<reference>
			<id>00:0B:FD:F3:A5:12</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>62</signalstrength>
		</reference>
		<reference>
			<id>00:0F:F8:58:E2:19</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>73</signalstrength>
		</reference>
		<reference>
			<id>00:0F:F8:58:E2:15</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>68</signalstrength>
		</reference>
		<reference>
			<id>00:07:50:D5:B1:D5</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>59</signalstrength>
		</reference>
		<reference>
			<id>00:07:50:D5:B1:C6</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>59</signalstrength>
		</reference>
		<reference>
			<id>00:0F:F8:58:E2:31</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>59</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="88" label="Ewenny" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T09:16:17</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:15:06Z</timestamp>
		<lat>51.497471</lat>
		<lon>-3.567313</lon>
		<accuracy>18.86</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4947387</id>
			<type>cell</type>
			<signalstrength>89</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="Ewenny" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>
<H3>Bookmarking a place</H3>
(notice that in the previous stanza his "cellpatternquality" is 100%. That means there is a good chance of this being a well defined pattern that he can be placed back at. <syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="set" id="setcurrent1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:set_current</value>
			</field>
			<field type="boolean" var="public">
				<value>true</value>
			</field>
			<field type="text-single" var="name">
				<value>Tesco bridgend</value>
			</field>
			<field type="text-single" var="street">
				<value/>
			</field>
			<field type="text-single" var="area">
				<value/>
			</field>
			<field type="text-single" var="city">
				<value>Bridgend</value>
			</field>
			<field type="text-single" var="postalcode">
				<value/>
			</field>
			<field type="text-single" var="region">
				<value/>
			</field>
			<field type="text-single" var="country">
				<value>United Kingdom</value>
			</field>
			<field type="text-single" var="longitude">
				<value>-3.567313</value>
			</field>
			<field type="text-single" var="latitude">
				<value>51.497471</value>
			</field>
			<field type="boolean" var="store">
				<value>true</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="setcurrent1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form"/>
		<field type="text-single" var="id">
			<value>4874</value>
		</field>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="placedetails1">
	<command xmlns="http://jabber.org/protocol/commands" node="place">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:place:get</value>
			</field>
			<field type="text-single" var="id">
				<value>4874</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="placedetails1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form"> <item>
			<field type="text-single" var="id">
				<value>4874</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="name">
				<value>Tesco bridgend</value>
			</field>
			<field type="text-single" var="district">
				<value>Bridgend null</value>
			</field>
			<field type="text-single" var="city">
				<value>Bridgend</value>
			</field>
			<field type="text-single" var="region">
				<value>Wales</value>
			</field>
			<field type="text-single" var="country">
				<value>United Kingdom</value>
			</field>
			<field type="text-single" var="latitude">
				<value>51.497471</value>
			</field>
			<field type="text-single" var="longitude">
				<value>-3.567313</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="subscribe1">
	<command xmlns="http://jabber.org/protocol/commands" node="place">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:place:subscribe</value>
			</field>
			<field type="text-single" var="id">
				<value>4874</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="subscribe1" type="result" xml:lang="en-GB"/>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
			<item>
			<field type="text-single" var="name">
				<value>Tesco bridgend</value>
			</field>
			<field type="text-single" var="id">
				<value>4874</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>1</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="set" id="next1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:set_next</value>
			</field>
			<field type="text-single" var="label">
				<value>Office</value>
			</field>
			<field type="text-single" var="place_id">
				<value>2195</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="next1" type="result" xml:lang="en-GB"/>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T09:19:08</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:18:16Z</timestamp>
		<lat>51.497733</lat>
		<lon>-3.565937</lon>
		<accuracy>35.53</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4947387</id>
			<type>cell</type>
			<signalstrength>77</signalstrength>
		</reference>
		<reference>
			<id>00:08:1E:00:0F:6D</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>84</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Near Tesco bridgend" placeid="4874" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T09:21:02</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:20:34Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937335</id>
			<type>cell</type>
			<signalstrength>102</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:20:35Z</timestamp>
		<lat>51.494301</lat>
		<lon>-3.557128</lon>
		<accuracy>90.68</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4947387</id>
			<type>cell</type>
			<signalstrength>102</signalstrength>
		</reference>
		<reference>
			<id>00:16:01:B0:3E:84</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>81</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Near Tesco bridgend" placeid="4874" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Near Tesco bridgend" placeid="4874" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:20:50Z</timestamp>
		<lat>51.493090</lat>
		<lon>-3.555175</lon>
		<accuracy>185.64</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937335</id>
			<type>cell</type>
			<signalstrength>99</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Ewenny" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:21:05Z</timestamp>
		<lat>51.491239</lat>
		<lon>-3.552231</lon>
		<accuracy>113.97</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937387</id>
			<type>cell</type>
			<signalstrength>92</signalstrength>
		</reference>
		<reference>
			<id>00:1F:1F:30:99:90</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>86</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Ewenny" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:21:35Z</timestamp>
		<lat>51.488635</lat>
		<lon>-3.547386</lon>
		<accuracy>146.07</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937335</id>
			<type>cell</type>
			<signalstrength>103</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Ewenny" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:22:05Z</timestamp>
		<lat>51.485796</lat>
		<lon>-3.540542</lon>
		<accuracy>105.56</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937387</id>
			<type>cell</type>
			<signalstrength>101</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Treoes" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:22:20Z</timestamp>
		<lat>51.484732</lat>
		<lon>-3.538076</lon>
		<accuracy>36.06</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4938861</id>
			<type>cell</type>
			<signalstrength>100</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Treoes" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:22:35Z</timestamp>
		<lat>51.483778</lat>
		<lon>-3.537116</lon>
		<accuracy>108.46</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937387</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:18:4D:00:82:74</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>99</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Treoes" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:23:05Z</timestamp>
		<lat>51.481630</lat>
		<lon>-3.531384</lon>
		<accuracy>118.54</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4927335</id>
			<type>cell</type>
			<signalstrength>93</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Treoes" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:23:20Z</timestamp>
		<lat>51.479772</lat>
		<lon>-3.524314</lon>
		<accuracy>63.46</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937387</id>
			<type>cell</type>
			<signalstrength>98</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Colwinston" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:23:35Z</timestamp>
		<lat>51.478392</lat>
		<lon>-3.518180</lon>
		<accuracy>57.20</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4938855</id>
			<type>cell</type>
			<signalstrength>87</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Colwinston" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:23:50Z</timestamp>
		<lat>51.476964</lat>
		<lon>-3.512801</lon>
		<accuracy>93.79</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4937864</id>
			<type>cell</type>
			<signalstrength>112</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:24:05Z</timestamp>
		<lat>51.475673</lat>
		<lon>-3.507837</lon>
		<accuracy>70.50</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:35275:4927335</id>
			<type>cell</type>
			<signalstrength>116</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:24:20Z</timestamp>
		<lat>51.474738</lat>
		<lon>-3.503217</lon>
		<accuracy>60.04</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:21746</id>
			<type>cell</type>
			<signalstrength>81</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Llangan" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="0" label="On the road in Llangan" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="On the road in Llangan" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:26:14Z</timestamp>
		<lat>51.460639</lat>
		<lon>-3.487264</lon>
		<accuracy>76.94</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>89</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="22" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq type="get" id="purplea27c8f31" to="kbateman@buddycloud.com/buddycloud">
	<query xmlns="jabber:iq:last"/>
</iq>
</syntaxhighlight>
<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:27:00Z</timestamp>
		<lat>51.455647</lat>
		<lon>-3.493147</lon>
		<accuracy>60.65</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:21746</id>
			<type>cell</type>
			<signalstrength>87</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="35" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:27:05Z</timestamp>
		<lat>51.455482</lat>
		<lon>-3.493023</lon>
		<accuracy>97.70</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:37565</id>
			<type>cell</type>
			<signalstrength>103</signalstrength>
		</reference>
		<reference>
			<id>00:1B:2F:38:6E:38</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>85</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="35" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:28:05Z</timestamp>
		<lat>51.448535</lat>
		<lon>-3.495190</lon>
		<accuracy>205.79</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:21746</id>
			<type>cell</type>
			<signalstrength>83</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="24" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:28:35Z</timestamp>
		<lat>51.445552</lat>
		<lon>-3.494648</lon>
		<accuracy>156.80</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:21422</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="24" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:29:05Z</timestamp>
		<lat>51.439606</lat>
		<lon>-3.492493</lon>
		<accuracy>119.25</accuracy>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>88</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="24" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:32:39Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>101</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>70</signalstrength>
		</reference>
		<reference>
			<id>00:18:4D:4F:E6:C0</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>88</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>42</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Llysworney" placeid="0" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
			<item>
			<field type="text-single" var="name">
				<value>Office</value>
			</field>
			<field type="text-single" var="id">
				<value>2195</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Travelling</value>
			</field>
			<field type="text-single" var="id">
				<value>2238</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T09:37:00</utc>
	</query>
</iq>
</syntaxhighlight> 
<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="subscriptions1">
	<command xmlns="http://jabber.org/protocol/commands" node="place">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:place:subscriptions</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="subscriptions1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
			<item>
			<field type="text-single" var="name">
				<value>Office</value>
			</field>
			<field type="text-single" var="id">
				<value>2195</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Travelling</value>
			</field>
			<field type="text-single" var="id">
				<value>2238</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Home 1.0</value>
			</field>
			<field type="text-single" var="id">
				<value>2477</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Radyr court inn</value>
			</field>
			<field type="text-single" var="id">
				<value>2613</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Radyr station car park</value>
			</field>
			<field type="text-single" var="id">
				<value>4256</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Home 2.0</value>
			</field>
			<field type="text-single" var="id">
				<value>4800</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Tesco bridgend</value>
			</field>
			<field type="text-single" var="id">
				<value>4874</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:35:52Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>98</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>64</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>68</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="kbateman@buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="mood1" type="result"/>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
			<item>
			<field type="text-single" var="name">
				<value>Office</value>
			</field>
			<field type="text-single" var="id">
				<value>2195</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Travelling</value>
			</field>
			<field type="text-single" var="id">
				<value>2238</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T09:38:40</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:38:53Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>79</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>68</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:40:24Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:31748</id>
			<type>cell</type>
			<signalstrength>101</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>66</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>68</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:40:39Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>96</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>71</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>74</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="88" label="On the road in Llysworney" placeid="0" state="moving"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T09:44:17</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:43:40Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>60</signalstrength>
		</reference>
		<reference>
			<id>00:14:6C:09:B1:DC</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>68</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>67</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>
<H3>Arriving at the office</H3>
<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="Near Office" placeid="2195" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
			<item>
			<field type="text-single" var="name">
				<value>Office</value>
			</field>
			<field type="text-single" var="id">
				<value>2195</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Travelling</value>
			</field>
			<field type="text-single" var="id">
				<value>2238</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:46:40Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>58</signalstrength>
		</reference>
		<reference>
			<id>00:14:6C:09:B1:DC</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>89</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>65</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="Near Office" placeid="2195" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:49:41Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>91</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>61</signalstrength>
		</reference>
		<reference>
			<id>00:14:6C:09:B1:DC</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>61</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>61</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="Near Office" placeid="2195" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:52:41Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>58</signalstrength>
		</reference>
		<reference>
			<id>00:14:6C:09:B1:DC</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>65</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>64</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="Near Office" placeid="2195" state="restless"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:54:09Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:31748</id>
			<type>cell</type>
			<signalstrength>100</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>68</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>60</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="set" id="next2">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:set_next</value>
			</field>
			<field type="text-single" var="label">
				<value/>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="next2" type="result" xml:lang="en-GB"/>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="100" label="Office" placeid="2195" state="stationary"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" id="nearbyplaces1">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<field type="hidden" var="FORM_TYPE">
				<value>buddycloud:location:places_near</value>
			</field>
		</x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="nearbyplaces1" type="result">
	<command xmlns="http://jabber.org/protocol/commands" node="location">
		<x xmlns="jabber:x:data" type="form">
			<reported>
				<field type="text-single" var="name"/>
				<field type="text-single" var="id"/>
				<field type="text-single" var="visibility"/>
				<field type="text-single" var="population"/>
			</reported>
			<item>
			<field type="text-single" var="name">
				<value>Office</value>
			</field>
			<field type="text-single" var="id">
				<value>2195</value>
			</field>
			<field type="text-single" var="visibility">
				<value>private</value>
			</field>
			<field type="text-single" var="population">
				<value>1</value>
			</field>
			</item> <item>
			<field type="text-single" var="name">
				<value>Travelling</value>
			</field>
			<field type="text-single" var="id">
				<value>2238</value>
			</field>
			<field type="text-single" var="visibility">
				<value>public</value>
			</field>
			<field type="text-single" var="population">
				<value>0</value>
			</field>
			</item> </x>
	</command>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:55:24Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>63</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>67</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Office" placeid="2195" state="stationary"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T09:58:25Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>62</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>57</signalstrength>
		</reference>
		<reference>
			<id>00:14:6C:09:B1:DC</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>57</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Office" placeid="2195" state="stationary"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T10:00:10Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:31748</id>
			<type>cell</type>
			<signalstrength>97</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>63</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>57</signalstrength>
		</reference>
		<reference>
			<id>00:14:6C:09:B1:DC</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>56</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Office" placeid="2195" state="stationary"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T10:00:58Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>97</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>62</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>58</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Office" placeid="2195" state="stationary"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T10:03:58Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>97</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>62</signalstrength>
		</reference>
		<reference>
			<id>00:14:6C:09:B1:DC</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>56</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>54</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Office" placeid="2195" state="stationary"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="buddycloud.com" type="get" id="time1">
	<query xmlns="jabber:iq:time"/>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="time1" type="result">
	<query xmlns="jabber:iq:time">
		<utc>20090326T10:06:41</utc>
	</query>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq to="butler.buddycloud.com" type="get" xml:lang="en" id="location1">
	<locationquery xmlns="urn:xmpp:locationquery:0">
		<timestamp>2009-03-26T10:06:59Z</timestamp>
		<publish>true</publish>
		<reference>
			<id>234:10:5160:30709</id>
			<type>cell</type>
			<signalstrength>94</signalstrength>
		</reference>
		<reference>
			<id>00:30:BD:F8:72:2C</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>60</signalstrength>
		</reference>
		<reference>
			<id>00:14:6C:09:B1:DC</id>
			<type>wifi</type>
			<enc>wep</enc>
			<signalstrength>90</signalstrength>
		</reference>
		<reference>
			<id>00:1E:2A:58:02:30</id>
			<type>wifi</type>
			<enc>wpa_psk</enc>
			<signalstrength>57</signalstrength>
		</reference>
	</locationquery>
</iq>
</syntaxhighlight>

<syntaxhighlight lang="XML">
<iq from="butler.buddycloud.com" to="kbateman@buddycloud.com/buddycloud" id="location1" type="result">
	<location xmlns="http://buddycloud.com/protocol/location" cellpatternquality="11" label="Office" placeid="2195" state="stationary"/>
</iq>
</syntaxhighlight>
