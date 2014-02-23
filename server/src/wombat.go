package octo

import (
	"appengine"
	"appengine/datastore"
	"crypto/md5"
	"encoding/json"
	"fmt"
	"net/http"
	"strings"
)

/**
 * Misc endpoints useful for TernGame '14 and maybe the future
 */

/* To enable these features, tweak these values: */
var WOMBAT_ENABLE = true
var WOMBAT_PASSWORD = "firefox"

/**
 answer_file (I was thinking of moving the data for the location of the
next puzzle into start_codes.json and specifying sequence there):
{
 "version" : 1,
 "answer_list" : [
     { "answer" : "wombat",
     "response" : "Great guess!  But not a correct one."
      },
     {
      "answer" : "wombatwombat",
      "response" : "Now you're really cooking... keep going..."
      } ,
    {
   "answer" : "wombatwombatwombat",
   "response" :"It's like you read my mind! Head to the ice cream shop
on 501 Octavia Street, hand the woman your \
    monopoly money (that's $500 mono-bucks, yo!) and see what you get
in return.",
    "correct" : true
    }
    ]
}
*/

func wombatact(w http.ResponseWriter, r *http.Request) {
	if WOMBAT_ENABLE != true {
		spewjsonp(w, r, MapSI{"wombat": "disabled"})
		return
	}
	if WOMBAT_PASSWORD != r.FormValue("wombatpassword") {
		spewjsonp(w, r, MapSI{"wombat": "bad wombatpassword"})
		return
	}
	context := appengine.NewContext(r)
	actID := r.FormValue("act")
	if actID == "" {
		spewjsonp(w, r, MapSI{"wombat": "need act=something"})
		return
	}
	key := datastore.NewKey(context, "Activity", actID, 0, nil)
	act := ActivityRecord{}
	err := datastore.Get(context, key, &act)
	if err == datastore.ErrNoSuchEntity {
		spewjsonp(w, r, MapSI{"wombat": "no such act"})
		return
	}
	if err != nil {
		spewjsonp(w, r, MapSI{"wombat": "err: " + err.Error()})
		return
	}
	var answer_list = []MapSI{}
	for ix, val := range act.Solutions {
		answer_list = append(answer_list, MapSI{
			"answer":    val,
			"correct":   true,
			"canonical": (ix == 0), // first answer in puz.txt is canonical
		})
	}
	for _, val := range act.Partials {
		split := strings.SplitN(val, " ", 2)
		if len(split) == 2 {
			answer_list = append(answer_list, MapSI{
				"answer":   split[0],
				"response": split[1],
				"correct":  false,
			})
		} else {
			answer_list = append(answer_list, MapSI{
				"answer":  split[0],
				"correct": false,
			})
		}
	}
	retval := MapSI{
		"raw":         act,
		"answer_list": answer_list,
		"title":       act.Title,
	}
	for _, val := range act.Extras {
		split := strings.SplitN(val, " ", 2)
		retval[split[0]] = split[1]
	}
	spewjsonp(w, r, retval)
}

/**
start_codes.json:
{
  "version" : 1,
  "start_codes" : [
     {
       "id" : "puzzle1",
       "name" : "orienteering",
       "answer_file" : "puzzle1_answers.json"

     },
     {
       "id" : "wombat",
       "name" : "God save the wombats",
       "answer_file" : "wombat_answers.json"
     }
   ]
}
*/
func wombatarc(w http.ResponseWriter, r *http.Request) {
	if WOMBAT_ENABLE != true {
		spewjsonp(w, r, MapSI{"wombat": "disabled"})
		return
	}
	if WOMBAT_PASSWORD != r.FormValue("wombatpassword") {
		spewjsonp(w, r, MapSI{"wombat": "bad wombatpassword"})
		return
	}
	context := appengine.NewContext(r)
	arcID := r.FormValue("arc")
	arc := fetcharc(context, arcID)
	retval := []MapSI{}
	for _, act := range arc.Act {
		retval = append(retval, MapSI{
			"id": act,
		})
	}
	spewjsonp(w, r, MapSI{
		"acts": retval,
	})
}

type WombatTeamStatusReceivedRecord struct {
	TeamName string
	Received []byte
}

type WombatParsedPuzzleStatus struct {
	Id         string   `json:"id"`
	StartTime  int      `json:"startTime"`
	EndTime    int      `json:"endTime"`
	Guesses    []string `json:"guesses"`
	HintsTaken []string `json:"hintsTaken"`
	Skipped    bool     `json:"skipped"`
	Solved     bool     `json:"solved"`
}

type WombatParsedTeamStatus struct {
	TeamName string                     `json:"teamName"`
	Puzzles  []WombatParsedPuzzleStatus `json:"puzzles"`
}

func AccumulateSummary(acc WombatParsedTeamStatus, rcvd []byte, context appengine.Context) (retval WombatParsedTeamStatus) {
	retval = acc
	parsed := WombatParsedTeamStatus{}
	err := json.Unmarshal(rcvd, &parsed)
	if err != nil {
		return
	}
	for ix, p := range parsed.Puzzles {
		if ix >= len(retval.Puzzles) {
			retval.Puzzles = append(retval.Puzzles, p)
			continue
		}
		if p.Id != retval.Puzzles[ix].Id {
			context.Warningf("panic, saw puzzle records out of order")
			return
		}
		if retval.Puzzles[ix].StartTime < 10 || (p.StartTime > 10 && p.StartTime < retval.Puzzles[ix].StartTime) {
			retval.Puzzles[ix].StartTime = p.StartTime
		}
		if retval.Puzzles[ix].EndTime < 10 || (p.EndTime > 10 && p.EndTime < retval.Puzzles[ix].EndTime) {
			retval.Puzzles[ix].EndTime = p.EndTime
		}
		retval.Puzzles[ix].Guesses = append(retval.Puzzles[ix].Guesses, p.Guesses...)
		retval.Puzzles[ix].HintsTaken = append(retval.Puzzles[ix].HintsTaken, p.HintsTaken...)
		retval.Puzzles[ix].Skipped = retval.Puzzles[ix].Skipped || p.Skipped
		retval.Puzzles[ix].Solved = retval.Puzzles[ix].Solved || p.Solved
	}
	return
}

func wombatteamstatus(w http.ResponseWriter, r *http.Request) {
	if WOMBAT_ENABLE != true {
		spewjsonp(w, r, MapSI{"wombat": "disabled"})
		return
	}
	if WOMBAT_PASSWORD != r.FormValue("wombatpassword") {
		spewjsonp(w, r, MapSI{"wombat": "bad wombatpassword"})
		return
	}
	complaints := []string{}
	received := []byte(r.FormValue("mystatus"))
	parsed := WombatParsedTeamStatus{}
	err := json.Unmarshal(received, &parsed)
	if err != nil {
		complaints = append(complaints, "JSON err2: "+err.Error())
	}
	teamName := parsed.TeamName
	if teamName == "" {
		complaints = append(complaints, "No team name found")
	}
	hash := md5.New()
	hash.Write(received)
	checksum := fmt.Sprintf("%x", hash.Sum(nil))
	context := appengine.NewContext(r)
	previous := []WombatTeamStatusReceivedRecord{}
	summary := WombatParsedTeamStatus{teamName, []WombatParsedPuzzleStatus{}}
	q := datastore.NewQuery("WombatTeamStatusReceived").
		Filter("TeamName =", teamName)
	_, err = q.GetAll(context, &previous)
	if err != nil {
		complaints = append(complaints, "failed to fetch previous: "+err.Error())
	}
	for _, v := range previous {
		summary = AccumulateSummary(summary, v.Received, context)
	}
	summary = AccumulateSummary(summary, received, context)
	key := datastore.NewKey(context, "WombatTeamStatusReceived", fmt.Sprintf("%v/%v", teamName, checksum), 0, nil)
	tsr := WombatTeamStatusReceivedRecord{
		teamName,
		received}
	_, err = datastore.Put(context, key, &tsr)
	debug := MapSI{
		"parsed":          parsed,
		"previousRecords": len(previous),
		"received":        string(received),
	}
	spewjsonp(w, r, MapSI{
		"DEBUG":      debug,
		"complaints": complaints,
		"summary":    summary,
	})
}
