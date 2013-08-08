package controllers

import play.api.mvc._
import play.libs.WS
import play.api.libs.json._
import org.joda.time.LocalTime
import scala.util.Try

object Application extends Controller {
  
  private val metOfficeKey = "1fd60563-da77-4bb9-88d5-0444be01310f"

  private val FALLBACK_LOCATIONS_DATA =
    """["Rosehearty Samos","Strathallan","Edinburgh/Gogarbank","Ronaldsway","St. Bees Head","Carlisle","Spadeadam",
      |"Leeming","Dishforth Airfield","Leconfield Sar","Wainfleet","Lydd","Humberside Airport","Sella Ness",
      |"Skye/Lusa (Samos)","Cassley","Aberdaron","Shawbury","Cottesmore","Marham","Trawsgoed","Sennybridge",
      |"Milford Haven C.B.","Filton","Brize Norton","Northolt","Liscombe","St-Athan","Odiham","Heathrow","Manston",
      |"Mount Batten","Jersey","Ballypatrick Forest","Larne","Coventry","Barrow-In-Furness","Bath","Bournemouth",
      |"Whitby","Aberfeldy","Chelmsford","Crewe","Exeter","Folkestone","Livingston","Lowestoft","Luton","Margate",
      |"Poole","Runcorn","Swindon","Aberdeen","Cowes","Brightlingsea","Wemyss Bay","Wisbech","Workington","Penryn",
      |"Salisbury","Enniskillen","Fishguard","Girvan","Goole","Hayes","Isle Of Grain","Croydon","Kingston Upon Thames",
      |"Hawick","Ampthill","Biggleswade","Cirencester","Ellesmere Port","Fort Augustus","Grantown-On-Spey","Hinckley",
      |"Kettering","Lockerbie","Machynlleth","Retford","Telford","Wakefield","Widnes","Winslow","Cromer","Formby",
      |"Greenhead Youth Hostel","Llandovery","Tiverton","Malmesbury","Teesport","Bishop Auckland","Hertford",
      |"Newton Stewart","Southwold","Tavistock","Durness","Kendal","Letchworth","Lochaline","Maldon","Nottingham",
      |"Rosyth","Scalloway","Scrabster","Seaham","Sharpness","Silloth","Hyde Park","London  Olympic Park South",
      |"Granton","Abbey Hulton","Abercarn","Aberconwy House","Aberdeen Youth Hostel","Aberdovey","Abertillery",
      |"Aberystwyth","Accrington","Achmelvich Youth Hostel","A'Chralaig","Adlington","Adventure Island",
      |"Ae Mountain Bike Trail Centre","Aintree","Alcester","Alderman's Green","Aldridge","Allesley","Alston",
      |"Alston Youth Hostel","Althorp House","Altrincham","Manchester","Worcester","Bideford",
      |"Cheadle (Staffordshire)","Glasgow/Bishopton","Eskdalemuir","Donna Nook","Whitstable","Kinbrace","Excel",
      |"Lairg","Lambourn","Land's End","Land's End Youth Hostel","Langdale Youth Hostel","Langholm","Langport",
      |"Langsett Youth Hostel","Langton Hall","Lanhydrock","Larmer Tree Gardens","Lauder","Laxford Bridge","Leeds",
      |"Leek","Legoland Windsor","Leicester City F.C.","Leicester Forest East","Leith Hall Garden And Estate",
      |"Lenton Abbey","Leonardslee Gardens","Leslie Hill Open Farm & Gardens","Letchworth Garden City","Levens Hall",
      |"Levenshulme","Lewes","Linford Wood","Lingfield Park Racecourse","Linstead Parva","Lisbellaw","Litherland",
      |"Little Bolton","Little Bromwich","Little Horton","Little Horwood","Litton Cheney Youth Hostel",
      |"Liverpool F.C.","Llanddona","Llanelli","Llanfyllin","Llangollen Youth Hostel","Llangolman","Llyn Y Fan Fawr",
      |"Loch A' Tuath","Loch Caolisport","Loch Carron","Loch Duich","Loch Eishort","Loch Lomond Youth Hostel",
      |"Loch Long","Loch Ossian Youth Hostel","Loch Tuath","Lochearnhead","Lockleaze","Lockton Youth Hostel",
      |"Loddiswell","Logan Botanic Garden","London Ashford Airport","London Gatwick Airport",
      |"Long Cross Victorian Gardens","Long Melford","Long Stratton","Longbridge Hayes","Longfleet",
      |"Lost Gardens Of Heligan","Loudon Castle Park","Lough Neagh","Low Team","Ludlow Youth Hostel",
      |"Lulworth Cove Youth Hostel","Lutterworth","Lydd","Lydney Park","Lynn Of Morven","Lytham St. Anne's","Maendy",
      |"Maesteg","Maghera","Alwoodley","Alyth","Am Bodach","A'Mharconaich","An Sgarsoch","An Socach (Glen Ey)",
      |"Anglesey Abbey","Antrim","Aonach Beag (Highland)","Apperley Bridge","Appley","Arbuthnott House","Ardlussa",
      |"Arlington Court","Armthorpe","Arno's Vale","Arundel","Ashbourne","Ashburton","Ashby De La Zouch",
      |"Ashton-In-Makerfield","Astley Bridge","Aston Villa F.C.","Atherstone","Atterton","Augher","Avebury",
      |"Avon Ski Centre","Ayr Racecourse","Bainton","Baldock","Ballater Ski Slope","Ballymena","Maghery Country Park",
      |"Malham Youth Hostel","Mallory Park","Maltby","Manchester Airport","Manchester City F.C.","Maney",
      |"Mangotsfield","Mankinholes Youth Hostel","March","Market Bosworth","Marple","Marwood Hill Gardens",
      |"Maryport","Masham","May Bank","Meall A' Choire Leith","Meall Chuaich","Meall Garbh (968m)","Meall Na Teanga",
      |"Meerbrook Youth Hostel","Meir","Melrose","Menai Strait","Mexborough","Mid Yell","Middleton (West Yorkshire)",
      |"Midlothian Ski Centre","Mill Rest Youth Hostel","Millbrook (Greater Manchester)","Millbrook (Southampton)",
      |"Bangor (Gwynedd)","Bangor Castle","Bangor Youth Hostel","Bannockburn","Bardsley","Bargany Gardens","Bargoed",
      |"Barley","Barnard Castle","Bartlow","Barton-Upon-Humber","Bath Youth Hostel","Batley","Batsford Arboretum",
      |"Battlefield","Beauly","Beaumaris","Becton","Bedgebury","Bedhampton","Bedworth","Beeston (Nottinghamshire)",
      |"Beeston (West Yorkshire)","Beinn A' Chroin","Beinn Bheoil","Beinn Chabhair","Beinn Eunaich",
      |"Beinn Iutharn Mhor","Beinn Liath Mhor","Beinn Liath Mhor Fannaich","Beinn Nan Aighenan","Beinn Narnain",
      |"Belgrave","Belhaven Bay","Belle Isle","Bellever Youth Hostel","Ben Hope","Ben More (Stirling)","Ben Starav",
      |"Minehead","Minehead Youth Hostel","Minnigaff Youth Hostel","Mirfield","Montacute House","Moor Head",
      |"Moor Side","Moray Monster Trails Mountain Bike Trail Centre","Morecambe","Moredun","Moreton","Morley",
      |"Morpeth","Mosborough","Moses Gate","Motcombe","Mountain Ash","Mr. B's Amusements Southend-On-Sea","Muirkirk",
      |"Mullach Na Dheiragain","Muscliff","Nantwich","Netheroyd Hill","New Addington","New Bolingbroke",
      |"New Brighton (Merseyside)","New Mills","New Ollerton","New Palace And Adventureland New Brighton","New Parks",
      |"New Quay","New Walton Pier","Newbridge","Ben Vane","Ben Vorlich (Argyll & Bute)","Beningbrough Hall",
      |"Bentley (South Yorkshire)","Bentley (West Midlands)","Bents Green","Berneray Youth Hostel","Bethersden",
      |"Bethesda","Beverley Racecourse","Bexley","Biddulph","Biddulph Grange Gardens","Biggin Hill","Billingshurst",
      |"Binbrook","Birmingham International Airport","Birstall Smithies","Bisham Abbey National Sports Centre",
      |"Black Sail Youth Hostel","Blackboys Youth Hostel","Blackburn Rovers F.C.","Blackgang Chine","Blackpool F.C.",
      |"Blackwood","Blairnairn","Blakelaw","Blantyre","Blossomfield","Bodnant Gardens","Bo'Ness","Borde Hill Garden",
      |"Borrowdale Youth Hostel","Borth Youth Hostel","Boston Deeps","Newcastle Racecourse","Newchurch","Newent",
      |"Newgale","Newnham Paddox Art Park","Newport-On-Tay","Newry","Newstead Abbey","Newton Abbot Racecourse",
      |"Newton Heath","Norristhorpe","North Shields","Northam","Northend","Northfleet","Northwood (Staffordshire)",
      |"Norton (North Yorkshire)","Norton (South Yorkshire)","Norton Conyers","Norton Woodseats",
      |"Nottingham Racecourse","Offerton","Old Trafford Lancashire C.C.C.","Oldbury","Oldhall",
      |"Once Brewed Youth Hostel","Osmondthorpe","Ossett","Otley","Ottery St. Mary","Out Skerries Airstrip","Ovenden",
      |"Oxford Youth Hostel","Paddock","Bothwell","Botley","Boughton House","Bowgreen","Bowood House & Gardens",
      |"Boyton Cross","Bracadale","Bracknell Ski & Snowboard Centre","Bradeley","Bradenham","Bradgate","Bradley Fold",
      |"Braeriach","Braigh Coire Chruinn-Bhalgain","Brampton","Branklyn Gardens","Braunstone","Breightmet",
      |"Brentwood Ski Centre","Bretton Youth Hostel","Bridgend (Bridgend)","Bridlington","Brincliffe","Brinnington",
      |"Brislington","Bristnall Fields","Bristol Filton Airport","Bristol International Airport","Briton Ferry",
      |"Broad Haven Youth Hostel","Brockenhurst","Bromley Ski Centre","Broughshane","Broughton Castle",
      |"Broughton House (Kirkcudbright)","Broughton In Furness","Bruera","Buckhaven","Budleigh Salterton","Bungay",
      |"Burley Youth Hostel","Pangbourne","Park Hill","Parkhall","Partick","Pashley Manor Gardens","Patchway",
      |"Patterson's Spade Mill (Nt)","Peacehaven","Peel Green","Pelynt","Penarth","Pendomer","Penhurst",
      |"Penrhiw-Llan","Pensnett","Penzance Youth Hostel","Perranporth Youth Hostel","Peterlee","Pinwherry","Pirton",
      |"Pitsmoor","Plas Newydd (Anglesey)","Plas-Y-Brenin National Centre For The Mountains",
      |"Pleasure Beach Great Yarmouth","Pleasurewood Hills Theme Park Lowestoft","Pleck","Pocklington","Polegate",
      |"Polesden Lacey","Pollokshaws","Pontardawe","Pontarddulais","Pontypool Ski Centre","Port St. Mary","Portadown",
      |"Portaferry","Porth","Burnden","Burton Coggles","Bushey","Bushmills","Butleigh","Bwlch Nant Yr Arian",
      |"Bynack More","Byrness Youth Hostel","Cairn Of Claise","Caldicot","Callington","Calne","Camberley",
      |"Camden Town","Camelford","Camperdown","Canklow","Cardiff City F.C.","Carn A' Gheoidh","Carn Gorm",
      |"Carn Mairg","Carn Nan Gobhar (Loch Mullardoch)","Carnforth","Carnoustie","Carnoustie Golf Links",
      |"Carrick-A-Rede Rope Bridge (Nt)","Castle Archdale Country Park","Castle Bromwich","Castle Coole (Nt)",
      |"Castle Hedingham Youth Hostel","Portobello (West Midlands)","Portsea","Portslade-By-Sea","Portsmouth F.C.",
      |"Postbridge","Poyntington","Prestatyn","Preston Manor","Prestonfield","Prior Park Bath","Prudhoe","Pulborough",
      |"Pwll Deri Youth Hostel","Pyle","Queensferry (Edinburgh)","R.H.S. Garden Hyde Hall","Randalstown","Rayleigh",
      |"Redbridge","Redcar","Redcar Racecourse","Redhill (Surrey)","Rhenigidale Youth Hostel",
      |"Richmond (Greater London)","Rickmansworth","Ringwood","Risca","Robin Hood Doncaster Sheffield Airport",
      |"Rochester","Rockingham Motor Speedway","Romford","Rosslea","Castle Ward (Nt)","Castleton Youth Hostel",
      |"Causeway Green","Chadwick Green","Chagford","Chapel Amble","Chapel Field","Chapel St. Leonards","Charlbury",
      |"Charleston Manor","Chartwell","Cheadle Hulme","Cheltenham Racecourse","Chigwell","Chilcompton","Childwall",
      |"Chillington","Chippenham","Christ's College Ski Club Guildford","Church Enstone","Clacton-On-Sea",
      |"Claydon House","Clayton-Le-Moors","Cleadon","Cleckheaton","Clevedon Court","Cleveleys",
      |"Clun Mill Youth Hostel","Clydebank","Clynnog-Fawr","Coalville","Cockermouth","Rothbury","Roundhay",
      |"Rowardennan Youth Hostel","Rowen Youth Hostel","Rowfoot","Rowley Regis","Rowley's Green",
      |"Royal Aberdeen Golf Club","Royal Botanic Gardens Edinburgh","Royal Lytham & St. Anne's Golf Club",
      |"Royal Troon Golf Club","Royal Tunbridge Wells","Royton","Rubery","Rugeley","Rumney","Rushall (Norfolk)",
      |"Ryde","Ryton","Sacriston","St Agnes","St Agnes Leisure Park","St Andrews","St Andrews Links",
      |"St David's Youth Hostel","St Fillans","St Helens","St Helier","St Just","St Leonards",
      |"St Mary's Pleasance Haddington","St Mellons Golf Club","St Neots","Sandbach","Sandend","Sandringham House",
      |"Sandwell","Sandwich","Schiehallion","Scotney Castle Garden","Scotswood","Scrabo Golf Club",
      |"Scunthorpe United F.C.","Colby Woodland Garden","Coleshill","Collyweston","Colsterworth","Combe Sydenham Hall",
      |"Compton Acres","Constable Burton Hall","Cookstown","Corstorphine","Corstorphine Hill","Cottingley",
      |"County Ground Derbyshire C.C.C.","Court House Green","Coveney","Cowbridge","Cowdenbeath","Coxlodge",
      |"Cradley (Herefordshire)","Cradley (West Midlands)","Cragside","Craig","Craig Youth Hostel","Craigavon",
      |"Craigie","Craigtown","Cramond","Cranborne Manor Gardens","Cranbrook","Craven Arms","Creag A' Mhaim",
      |"Crealy Adventure Park Devon","Crew's Hole","Crianlarich Youth Hostel","Criccieth","Crickhowell",
      |"Crimonmogate","Crofts Bank","Crofts Of Benachielt","Cross Hands","Crossens","Crosswell","Crownhill",
      |"Croxteth Hall","Cruach Nan Capull","Crystal Palace National Sports Centre","Cudmore Grove","Cudworth",
      |"Seaton","Selkirk","Sgor Gaoith","Sgurr A' Choire Ghlais","Sgurr A' Ghreadaidh","Sgurr A' Mhaoraich",
      |"Sgurr An Lochain","Sgurr Dearg","Sgurr Fhuaran","Sgurr Fiona","Sgurr Mor (1003m)","Sgurr Na Ciche",
      |"Sgurr Nan Coireachan (Glen Finnan)","Sgurr Nan Conbhairean","Sgurr Nan Each","Sgurr Nan Eag","Sgurr Thuilm",
      |"Shaw","Sheerness","Sheffield","Sheffield Lane Top","Sheffield Ski Village","Shefford","Sheldon",
      |"Sheldon Manor","Shepshed","Sheringham Park","Sheringham Youth Hostel","Shoreham-By-Sea","Short Heath",
      |"Silsden","Cullompton","Cumrew","Curlew Green","Dagenham","Daisy Bank","Darlington","Darsham",
      |"Dartington Youth Hostel","Dartmeet","Davenport","David Welch Winter Gardens","Dawlish","Deane","Denby Dale",
      |"Denford","Derry Cairngorm","Dimsdale","Dinas Powys","Dinosaur Adventure Park Norfolk","Doffcocker",
      |"Donaghadee","Doncaster Rovers Fc","Donington Park","Down House","Down St. Mary","Downpatrick Racecourse",
      |"Dumbreck","Dundee F.C.","Somerleyton Hall & Gardens","Sound Of Mingulay","Southbourne","Southport",
      |"Springburn","Stainforth (North Yorkshire)","Standon","Start Bay","Stedham","Stenhouse","Stepney",
      |"Steps Bridge Youth Hostel","Stirling Youth Hostel (Union Street)","Dunning","Dunstable","East Howe",
      |"East Moor","Eastham","Eastwood (Nottinghamshire)","Eccleshill","Egremont","Elie","Elton Youth Hostel","Epsom",
      |"Erskine","Evertown","Exford Youth Hostel","Eye (Peterborough)","Failsworth","Fairfield (Nr Bury)",
      |"Stob Binnein","Stob Choire Raineach","Stob Coire Sgreamhach","Stoneclough","Stoneygate","Storrington",
      |"Stratford Racecourse","Street","Stromness Youth Hostel","Stronsay Airfield","Stubshaw Cross",
      |"Sunderland A.F.C.","Sunderland Ski Centre","Swinton (South Yorkshire)","Syon House","Tannadice","Tetbury",
      |"Far Moor","Far Royds","Faringdon","Felbrigg Hall","Feltwell","Finlaystone House","Fionn Bheinn",
      |"Fishguard Bay","Fishpool","Forfar","Foula Airstrip","Fulham F.C.","Fulwood","Gairich","Gatley",
      |"Geal Charn (926m)","Gilbertstone","Glas Maol","Glasgow Prestwick Airport","Thackley","The Belfry Golf Club",
      |"The County Ground Somerset C.C.C.","The Hirsel","Hampshire C.C.C.","Thirlmere Youth Hostel",
      |"Thirsk Racecourse","Thornhill (Dumfries & Galloway)","Three Counties Showground","Tidworth",
      |"Tomatin Distillery","Top Of Hebers","Torkington","Towcester Racecourse","Treherbert",
      |"Glenbrittle Youth Hostel","Glencoe Mountain Resort","Glendoll Youth Hostel","Glendurgan Garden",
      |"Glentress","Gleouraich","Gloucestershire Airport","Glyndebourne","Gnosall","Godalming","Golborne",
      |"Golds Green","Golspie","Goltho House Gardens","Goose Green","Gorbals","Gowerton","Great Barr",
      |"Great Ellingham","Great Horton","Greenhill","Greenwood Forest Park (Y Felinheli)","Grey Abbey Physic Garden",
      |"Gruinard Bay","Haddo House","Halesworth","Hall Green","Treorchy","Treyarnon Bay Youth Hostel","Tuckton",
      |"Uig Youth Hostel","Ullapool Youth Hostel","Upton (Hampshire)","Utley","Wadebridge","Walkden","Wallsend",
      |"Waltham Abbey","Wandsworth","Wantage","Watersheddings","Watford (Hertfordshire)","Wednesbury","Wellbank",
      |"Wells Green","Wembley","Wembury","Hare Hill","Harlech","Harrow","Harwood","Harwood Lee",
      |"Hatfield (Hertfordshire)","Haverfordwest","Hay Mills","Hazelhurst","Headingley","Healey",
      |"Heaton (West Yorkshire)","Heckfield","Hereford Racecourse","Heyrod","Highcliffe","West Lavington",
      |"West Woodlands","Westminster","Westville","Whitecote","Whitefield","Whitepark Bay Youth Hostel","Whitleigh",
      |"Whitsand Bay","Wicksteed Park","Wide Firth","Widecombe In The Moor","Willen","Willington (Tyne & Wear)",
      |"Wilmslow","Wilton","Windermere","Woburn Abbey","Holmbury St. Mary Youth Hostel","Holywood","Horton Green",
      |"Hoy Youth Hostel","Huntly","Iford","Ilkley","Inverey Youth Hostel","Inverguseran","Wolstanton","Wombourne",
      |"Wooler","Woolfold","Worthing","Wragby","Wreay","Wroxton Abbey","Wycombe Summit Ski & Snowboarding Centre",
      |"Yeovil","Youlgreave Youth Hostel","Kemble","Kendal Youth Hostel","Kendoon Youth Hostel",
      |"Kershader Youth Hostel","Kilbirnie","Kilkhampton","Kilrea","Kingsteignton","Kingston Upon Hull",
      |"Kirk Yetholm Youth Hostel","Kirkby Green","Knebworth House","Knowle (Bristol)","Porthleven - West (Beach)",
      |"Gorran Haven - Vault Beach (Beach)","Pentewan Sands (Beach)","Saunton Sands (Beach)",
      |"Thurlestone - South (Beach)","Paignton - Preston Sands (Beach)","Watcombe (Beach)","Ringstead Bay (Beach)",
      |"Studland - Knoll Beach (Beach)","Poole Harbour - Hamworthy Park (Beach)","Colwell Bay (Beach)",
      |"Whitecliff Bay (Beach)","Milford-On-Sea (Beach)","Lee-On-Solent (Beach)","Bracklesham Bay (Beach)",
      |"Selsey (Beach)","Littlehampton (Beach)","Pevensey Bay (Beach)","Hythe (Beach)","Sandwich Bay (Beach)",
      |"Southend - Thorpe Bay (Beach)","Brightlingsea (Beach)","Caister Point (Beach)","Hemsby (Beach)",
      |"Chapel St Leonards (Beach)","Moggs Eye (Huttoft Beach) (Beach)","Mablethorpe Town (Beach)","Reighton (Beach)",
      |"Scarborough North Bay (Beach)","Whitby (Beach)","Sandsend (Beach)","Llanddulas (Beach)","Church Bay (Beach)",
      |"Aber Mawr Bay (Beach)","Grouville (Beach)","Portelet (Beach)","Embo Beach","Achmelvich Bay (Beach)",
      |"Cullen (Beach)","Dunure (Beach)","Saltcoats (Beach)","Gourock (Beach)","Culzean (Beach)",
      |"Peterhead Lido (Beach)","Carnoustie (Beach)","Kinghorn - Pettycur (Beach)","Gullane (Beach)",
      |"Seacliff (Beach)","Porthcawl - Rest Bay (Beach)","Coppet Hall (Beach)","Broadhaven (Beach)",
      |"Whitesands (Beach)","Croyde (Beach)","Woolacombe (Beach)","Weston Super Mare - Sand Bay (Beach)",
      |"Burnham-On-Sea - Jetty (Beach)","Newquay - Fistral (Beach)","The Towans - Godrevy (Beach)","Kinloss",
      |"Wick Airport","Aboyne","Islay/Port Ellen","Glen Ogle","Charterhall","Keswick","Albemarle","Topcliffe",
      |"Fylingdales","Bridlington Mrsc","Capel Curig Saws","Leek","Scampton","Coningsby","Cranfield",
      |"Liverpool John Lennon Airport","Sheffield Airport","Baltasound","Lerwick (S. Screen)","Loch Glascarnoch Saws",
      |"Altnaharra Saws","Wittering","Aberporth","Hereford","Pershore","Benson","High Wycombe","Shoeburyness",
      |"Larkhill","Boscombe Down","Langdon Bay","Scilly St Marys","Camborne","Culdrose","Cardinham",
      |"Dunkeswell Aerodrome","Isle Of Portland","Hurn","Thorney Island","Solent Mrsc","Herstmonceux West End",
      |"Ballykelly Samos","Birmingham","Bradford","Leicester","Plymouth","Blackburn","Bognor Regis","Bracknell",
      |"Aylesbury","Banbury","Barnstaple","Lancaster","Pershore","Ardrossan","Corby","Douglas (Isle Of Man)",
      |"Gillingham (Medway)","Gloucester","Greenock","Grimsby","Norwich","Peterborough","Portsmouth","Campbeltown",
      |"Elgin","Battle","Bodmin","Bottesford","Bowling","Bridport","Charlestown","Newquay","St. Ives","Fleetwood",
      |"Hoddesdon","Aldershot","Alloa","Betws-Y-Coed","Bridgnorth","Devizes","Harrogate","Hungerford","Macclesfield",
      |"Mansfield","North Berwick","Pontefract","Stenhousemuir","Tewkesbury","Woking","Darwen","Bolton","Petersfield",
      |"Swaffham","Chard","Lynmouth","Askrigg","Stonehouse","Dorking","Horsham","Aberaeron","Kingsnorth",
      |"Littlehampton","Ramsgate","Exeter Airport","Arinagour Isle Of Coll","Abbotsbury Sub-Tropical Gardens",
      |"Achininver Youth Hostel","Alford (Lincolnshire)","Allerton (West Yorkshire)","Oldham","Huntingdon",
      |"Waddington","Arbroath","Dunstaffnage","Millwall F.C.","Brighton And Hove Albion","Livingston F.C.",
      |"Wembley Arena","Bowling","Laisterdyke","Langtoft","Lapal","Larkhall","Laxey","Laxey Glen","Ledbury",
      |"Leeds Castle Gardens","Leicester Racecourse","Leominster","Leven","Leverton","Lichfield","Lilliput",
      |"Limavady","Liversedge","Llanberis","Llanberis Youth Hostel","Llandaff","Llandeilo",
      |"Llandudno Ski & Snowboard Centre","Llandysul","Llanfair Caereinion","Loch Ewe","Loch Hourn","Loch Na Keal",
      |"Lochgelly","Lochnagar","London Luton Airport","London Southend Airport","Long Sutton","Long Thurlow",
      |"Longden","Loseley House","Lotherton Hall","Low Bradfield","Lurg Mhor","Lymm","Lyndhurst",
      |"Maeshafn Youth Hostel","Magheraveely","Am Basteir","An Socach (Highland)","Aonach Beag (1238m)",
      |"Arbourthorne","Ardanaiseig Gardens","Ardchattan Gardens","Ardencraig Gardens","Armathwaite","Auchinleck",
      |"Auchtermuchty","Aviemore","Baldersdale Youth Hostel","Ballantrae Bay","Ballymoney","Bamber Bridge","Malton",
      |"Malvern Hills Youth Hostel","Mannings Amusement Park","Maol Chinn-Dearg","Mapperton Gardens","Greenwich Park",
      |"Markethill","Marloes Sands Youth Hostel","Marsden (Tyne & Wear)","Maryhill","Marylebone","Mavis Enderby",
      |"Mayar","Mcarthur's Head Lighthouse","Meall Buidhe (Highland)","Meall Nan Eun","Melford Hall","Menai Bridge",
      |"Mere","Mevagissey","Middleton (Greater Manchester)","Milford On Sea","Millhouses","Millom","Milnrow",
      |"Bangor (North Down)","Bangor-On-Dee Racecourse","Barra (Traigh Mh�r) Airport","Bateman's","Bathgate",
      |"Beaminster","Bearsden","Bearsden Ski Centre","Beauchief","Bedgebury National Pinetum","Bedlington",
      |"Bedminster","Beech Lane","Beinn A' Chreachain","Beinn Bhrotain","Beinn Eibhinn","Beinn Fhada",
      |"Beinn Fhionnlaidh (Argyll & Bute)","Beinn Ghlas","Beinn Heasgarnich","Beinn Udlamain","Beith",
      |"Ben More (Argyll & Bute)","Moorside (Nr Oldham)","Moorside (West Yorkshire)","Moss Nook","Moston",
      |"Mottisfont Abbey","Mount Keen","Mount Vernon","Nairn","Nether Edge","New Luce","New Moat","New Pitsligo"
      |]""".stripMargin

  private lazy val locationsJson: String = {
    Try {
      val responseFuture = WS.url("http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/sitelist")
        .setQueryParameter("key", metOfficeKey)
        .get
      val root = Json.parse(responseFuture.get.getBody)
      val locations = (root \ "Locations" \ "Location").asOpt[List[Map[String, String]]].getOrElse(List())
      val sortedLocations = locations.map(x => x("name")).sorted
      val json = Json.toJson(sortedLocations)
      Json.stringify(json)
    }.getOrElse(FALLBACK_LOCATIONS_DATA)
  }

  private val FALLBACK_WEATHER_DATA =
    """[
      | {"T":"20","U":"1","F":"18","G":"16","hours":"18","V":"EX","date":"2013-08-08Z","Pp":"10","H":"54","W":"7","D":"SSW","S":"9"},
      | {"T":"15","U":"0","F":"15","G":"7","hours":"21","V":"VG","date":"2013-08-08Z","Pp":"2","H":"73","W":"2","D":"SE","S":"0"},
      | {"T":"15","U":"0","F":"15","G":"7","hours":"0","V":"VG","date":"2013-08-09Z","Pp":"15","H":"77","W":"2","D":"SSE","S":"4"},
      | {"T":"15","U":"0","F":"15","G":"4","hours":"3","V":"GO","date":"2013-08-09Z","Pp":"19","H":"86","W":"7","D":"SE","S":"2"},
      | {"T":"15","U":"1","F":"14","G":"9","hours":"6","V":"GO","date":"2013-08-09Z","Pp":"8","H":"88","W":"7","D":"WSW","S":"7"},
      | {"T":"16","U":"2","F":"15","G":"18","hours":"9","V":"VG","date":"2013-08-09Z","Pp":"6","H":"81","W":"7","D":"W","S":"11"},
      | {"T":"18","U":"4","F":"15","G":"22","hours":"12","V":"VG","date":"2013-08-09Z","Pp":"15","H":"70","W":"7","D":"W","S":"13"},
      | {"T":"18","U":"3","F":"15","G":"27","hours":"15","V":"VG","date":"2013-08-09Z","Pp":"10","H":"61","W":"3","D":"W","S":"16"},
      | {"T":"17","U":"1","F":"14","G":"25","hours":"18","V":"VG","date":"2013-08-09Z","Pp":"1","H":"61","W":"3","D":"W","S":"13"},
      | {"T":"14","U":"0","F":"12","G":"22","hours":"21","V":"VG","date":"2013-08-09Z","Pp":"1","H":"70","W":"0","D":"W","S":"11"},
      | {"T":"12","U":"0","F":"10","G":"18","hours":"0","V":"VG","date":"2013-08-10Z","Pp":"1","H":"80","W":"0","D":"WSW","S":"9"},
      | {"T":"11","U":"0","F":"9","G":"16","hours":"3","V":"VG","date":"2013-08-10Z","Pp":"1","H":"87","W":"0","D":"WSW","S":"9"},
      | {"T":"11","U":"1","F":"10","G":"13","hours":"6","V":"GO","date":"2013-08-10Z","Pp":"1","H":"87","W":"1","D":"WSW","S":"7"},
      | {"T":"14","U":"2","F":"13","G":"16","hours":"9","V":"VG","date":"2013-08-10Z","Pp":"4","H":"72","W":"7","D":"WSW","S":"9"},
      | {"T":"16","U":"4","F":"14","G":"20","hours":"12","V":"VG","date":"2013-08-10Z","Pp":"9","H":"69","W":"7","D":"WSW","S":"9"},
      | {"T":"16","U":"2","F":"14","G":"20","hours":"15","V":"VG","date":"2013-08-10Z","Pp":"15","H":"71","W":"7","D":"WSW","S":"11"},
      | {"T":"15","U":"1","F":"14","G":"18","hours":"18","V":"VG","date":"2013-08-10Z","Pp":"14","H":"74","W":"7","D":"WSW","S":"9"},
      | {"T":"14","U":"0","F":"13","G":"13","hours":"21","V":"GO","date":"2013-08-10Z","Pp":"28","H":"81","W":"9","D":"SW","S":"7"},
      | {"T":"13","U":"0","F":"12","G":"16","hours":"0","V":"GO","date":"2013-08-11Z","Pp":"4","H":"89","W":"2","D":"SW","S":"9"},
      | {"T":"12","U":"0","F":"11","G":"18","hours":"3","V":"GO","date":"2013-08-11Z","Pp":"10","H":"90","W":"2","D":"WSW","S":"9"},
      | {"T":"12","U":"1","F":"11","G":"18","hours":"6","V":"GO","date":"2013-08-11Z","Pp":"3","H":"87","W":"3","D":"WSW","S":"11"},
      | {"T":"14","U":"2","F":"12","G":"25","hours":"9","V":"GO","date":"2013-08-11Z","Pp":"35","H":"79","W":"10","D":"WSW","S":"13"},
      | {"T":"16","U":"4","F":"13","G":"31","hours":"12","V":"VG","date":"2013-08-11Z","Pp":"39","H":"65","W":"10","D":"WSW","S":"16"},
      | {"T":"15","U":"2","F":"12","G":"29","hours":"15","V":"GO","date":"2013-08-11Z","Pp":"38","H":"71","W":"10","D":"WSW","S":"16"},
      | {"T":"14","U":"1","F":"11","G":"31","hours":"18","V":"VG","date":"2013-08-11Z","Pp":"9","H":"72","W":"3","D":"WSW","S":"16"},
      | {"T":"12","U":"0","F":"10","G":"25","hours":"21","V":"VG","date":"2013-08-11Z","Pp":"10","H":"82","W":"0","D":"WSW","S":"13"},
      | {"T":"12","U":"0","F":"9","G":"25","hours":"0","V":"VG","date":"2013-08-12Z","Pp":"1","H":"87","W":"2","D":"WSW","S":"13"},
      | {"T":"11","U":"0","F":"9","G":"25","hours":"3","V":"VG","date":"2013-08-12Z","Pp":"0","H":"86","W":"0","D":"W","S":"13"},
      | {"T":"12","U":"1","F":"9","G":"22","hours":"6","V":"VG","date":"2013-08-12Z","Pp":"0","H":"84","W":"3","D":"W","S":"13"},
      | {"T":"14","U":"2","F":"11","G":"27","hours":"9","V":"VG","date":"2013-08-12Z","Pp":"11","H":"72","W":"3","D":"W","S":"16"},
      | {"T":"15","U":"4","F":"13","G":"29","hours":"12","V":"VG","date":"2013-08-12Z","Pp":"13","H":"63","W":"3","D":"WNW","S":"16"},
      | {"T":"16","U":"2","F":"13","G":"27","hours":"15","V":"VG","date":"2013-08-12Z","Pp":"17","H":"61","W":"7","D":"WNW","S":"16"},
      | {"T":"15","U":"1","F":"13","G":"25","hours":"18","V":"VG","date":"2013-08-12Z","Pp":"12","H":"65","W":"3","D":"WNW","S":"13"},
      | {"T":"13","U":"0","F":"11","G":"20","hours":"21","V":"VG","date":"2013-08-12Z","Pp":"14","H":"75","W":"7","D":"W","S":"11"}
      | ]""".stripMargin

  private def weatherForLocation(locationId: Int): String = {
    Try {
      val responseFuture = WS.url(s"http://datapoint.metoffice.gov.uk/public/data/val/wxfcs/all/json/$locationId")
         .setQueryParameter("res", "3hourly")
         .setQueryParameter("key", metOfficeKey)
         .get
       val root = Json.parse(responseFuture.get.getBody)
       val metadata: Map[String, JsValue] = (root \ "SiteRep" \ "DV").asOpt[Map[String, JsValue]].getOrElse(Map())
       val periods: List[Map[String, JsValue]] = (metadata("Location") \ "Period").asOpt[List[Map[String, JsValue]]].getOrElse(List())
       val data: List[Map[String, String]] = for {
         period <- periods
         rep <- period("Rep").asOpt[List[Map[String, String]]].getOrElse(List[Map[String,String]]())
       } yield {
         val combined = rep ++ Map("date" -> period("value").as[String])
         combined.keys.map {
           (key: String) =>
             val value = combined(key)
             key match {
               case "$" =>
                 val mins = value.toInt
                 val time = LocalTime.fromMillisOfDay(mins * 60 * 1000)
                 ("hours", time.getHourOfDay.toString)
               case x => (x, value)
             }
         }.toMap
       }
       val json = Json.toJson(data)
       Json.stringify(json)
    }.getOrElse(FALLBACK_WEATHER_DATA)
  }

  def index = Action {
    Ok(views.html.index(""))
  }

  def locationsFeed = Action {
    Ok(locationsJson)
  }

  def weatherFeed(locationId: Int) = Action {
    Ok(weatherForLocation(locationId))
  }
  
}
