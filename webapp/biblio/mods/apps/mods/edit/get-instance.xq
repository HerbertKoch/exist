xquery version "1.0";
import module namespace style = "http://www.danmccreary.com/library" at "../../../modules/style.xqm";
declare namespace mods = "http://www.loc.gov/mods/v3";

(:
There are many sub-parts to the form.  Each part needs to map the right part of an instance into one tab
:)

declare namespace xf="http://www.w3.org/2002/xforms";
declare namespace xforms="http://www.w3.org/2002/xforms";
declare namespace ev="http://www.w3.org/2001/xml-events";

(: This is the document we are going to edit if we are not creating a new record :)
let $id := request:get-parameter('id', '')
let $new := request:get-parameter('new', 'true')
let $debug := request:get-parameter('debug', 'false')

let $app-collection := $style:db-path-to-app
let $data-collection := $style:db-path-to-app-data

(: If we are creating a new form, then just get the part of the new-instance.  Else get the data from the correct document
   in the mods data collection that has the correct collection id :)
let $instance :=
   if ($new = 'true')
      then doc(concat($style:db-path-to-app, '/edit/new-instance.xml'))/mods:mods
      else collection($data-collection)/mods:mods[$id = id]
      
let $tab-data := doc(concat($style:db-path-to-app, '/edit/tab-data.xml'))/tabs

(: lookup the XPath expressions for this tab-id in the tab database :)
let $tab := $tab-data/tab[tab-id = $id]

(: get a list of all the XPath expressions to include in this instance used by the form :)
let $paths := $tab-data/tab[tab-id = $id]/path/text()

(: build up a string of prefix:element pairs for doing an eval :)
let $path-string :=
string-join(
    for $path in $paths
    return 
      concat('mods:', $path)
  , ', ')

(: now get the eval string ready for use :)
let $eval-string := concat('$instance/', '(', $path-string, ')')

return
<mods:mods>

  { (: this is used for debuggin only.  Just add the debug=true to the URL and it will be added to the output :)
  if ($debug = 'true')
    then <debug>
      <tab>{$tab}</tab>
      <paths>{$paths}</paths>
      <eval-string>{$eval-string}</eval-string>
    </debug> else ()
  }
  
  (: this is where we run the query that gets just the data we need for this tab :)
  {util:eval($eval-string)}
  
  </mods:mods>
   