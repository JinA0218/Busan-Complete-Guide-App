# <image src="./app/src/main/res/drawable/travel2.png" width="45"/> Busan Complete Guide


## Development Team  
- **Shin Jongmin**, Pusan National University, Class of 2019  
- **Jina Kim**, KAIST, School of Freshmen  

---

## Development Environment 
Client)
- OS: Android (minSdk: 26, targetSdk: 32)
- Language: Java
- IDE: Android Studio
- Target Device: Galaxy S7

Server)
- Stack: Nodejs
- Language: javascript
- Framework: Express
- DataBase: Mysql


---

## Introduction 
<image src="./app/src/main/res/drawable/s0.png" width="200"/> 

The **“Busan Complete Guide”** app provides recommendations for **Busan restaurants, attractions, and festivals**.  

Users can check all “liked” items from restaurants, attractions, and festivals in one place under the **“함 봐라 (Check it out)”** tab.  

---

## Features  

### 1. Sign Up, Login, Profile Update  

<div>

<image src="./app/src/main/res/drawable/s1.png" width="200"/>

<image src="./app/src/main/res/drawable/s2.png" width="200"/>

<image src="./app/src/main/res/drawable/s3.png" width="200"/>

<image src="./app/src/main/res/drawable/s18.png" width="200"/>

</div>

- On first launch, the app displays a **Splash screen** and moves to the **Login tab**.  
- If the user has no account, they can proceed to the **Sign Up tab** to register.  
- After login, the user is directed to the **FirstPage**, which displays:  
  - Greeting message: `username + "어서온나!"` (Welcome!)  
  - Current Busan weather  
  - Buttons for **Restaurants, Attractions, Festivals**  
- The **top-right button** allows the user to update profile information.  

---

<div>

<image src="./app/src/main/res/drawable/s4.png" width="200"/>

</div>

### 2. “뭐 물래?”(What do you want to eat?) : Restaurant Recommendation

<div>

<image src="./app/src/main/res/drawable/s5.png" width="200"/>
<image src="./app/src/main/res/drawable/s6.png" width="200"/>
<image src="./app/src/main/res/drawable/s7.png" width="200"/>
<image src="./app/src/main/res/drawable/s8.png" width="200"/>
<image src="./app/src/main/res/drawable/s81.png" width="200"/>

- Clicking the **뭐 물래?** button moves to **BusanFoodStep1**.  
- Users select **1–3 keywords**, and the app returns restaurant information via a **ListView**.  

**Tag Function**  
- When a tag is clicked, the app calls the `addTag` method, sending selected tags as JSON to the DB.  
- The server returns matching restaurant data as JSON, which is displayed in a ListView.  

**Example API:**  

    ```sql
    app.post('/getFoodByTags', (req, res) => {
        console.log("getFoodByTags 요청 시작 !!!!");
        console.log(req.body);
        var jsonArraylist = new Array();
        var jsonArray = {
            body: ''
        };
        var sql = `select * from food_mst where tag1 like ` + connection.escape('%' + req.body.tag1 + '%');
        if (req.body.count == 0) {
            console.log("태그 0개");
            var sql = `select * from food_mst where id = 0;`;
        } else if (req.body.count == 1) {
            console.log("태그 1개");
            var sql = `select * from food_mst where tag1 like ` + connection.escape('%' + req.body.tag1 + '%') + `;`;
        }
        else if (req.body.count == 2) {
            console.log("태그 2개");
            var sql = `select * from food_mst where tag1 like ` + connection.escape('%' + req.body.tag1 + '%') + ` and tag1 like ` + connection.escape('%' + req.body.tag2 + '%') + `;`;
        }
        else if (req.body.count == 3) {
            console.log("태그 3개");
            var sql = `select * from food_mst where tag1 like ` + connection.escape('%' + req.body.tag1 + '%') + ` and tag1 like ` + connection.escape('%' + req.body.tag2 + '%') + ` and tag1 like ` + connection.escape('%' + req.body.tag3 + '%') + `;`;
        }
    
        //var sql = ;
        var values = [req.body.tag1, req.body.tag2, req.body.tag3];
        connection.query(sql, values, (error, rows) => {
            if (error) {
                throw error;
    
            }
            console.log('select 성공 !!!!!!!!');
            if (rows.length == 0) {
                console.log('Json object : ', json_object_send);
                res.send(json_object_send);
                return;
            }
            console.log("가져온거 갯수" + rows.length);
            for (let i = 0; i < rows.length; i++) {
                console.log(rows[i]["tel"]);
                var json_object_send = {
                    id: rows[i]["id"],
                    mainTitle: rows[i]["main_title"],
                    place: rows[i]["place"],
                    subTitle: rows[i]["sub_title"],
                    img: rows[i]["img"],
                    context: rows[i]["context"],
                    tag1: rows[i]["tag1"],
                    latitude: rows[i]["latitude"],
                    longitude: rows[i]["longitude"],
                    tag2: '',
                    tag3: '',
                    tel: rows[i]["tel"]
                };
                jsonArraylist.push(json_object_send);
            }
    
            setTimeout(() => {
                var strJson = JSON.stringify(jsonArraylist);
                jsonArray.body = strJson;
                res.send(jsonArray);
            }, 200);
    
        });
    });
    ```
    
    - **태그** 3개로 음식 정보 받아오기
    
    ```sql
    app.post('/likeFestivalList', (req, res) => {
        console.log("likeFestivalList 요청 시작 !!!!");
        console.log(req.body);
        var likeList = new Array();
        var jsonArraylist = new Array();
        var jsonArray = {
            body: ''
        };
        var sql = `select festival from festival_like_mst where user_id = ?`;
        var values = [req.body.userId];
        connection.query(sql, values, (error, rows) => {
            if (error) {
                throw error;
    
            }
            console.log('select 성공 !!!!!!!!');
            for (let i = 0; i < rows.length; i++) {
                var jsontemp = {
                    festival_id: rows[i]["festival"] + ""
                }
    
                likeList.push(jsontemp);
                console.log(rows[i]["festival"]);
            }
    
        });
        setTimeout(() => {
            var strJson = JSON.stringify(likeList);
            jsonArray.body = strJson;
            console.log(jsonArray);
            res.send(jsonArray);
        }, 200);
    });
    ```

**Restaurant Details**

- Selecting an item from the list moves to FoodFullImage, where the following are displayed:
    - Title, Subtitle, Place, Image, Context, Tags, Latitude, Longitude, Telephone
    - **BusanFoodDto**
    
    ```jsx
    package com.example.whatmain;
    
    import java.io.Serializable;
    
    public class BusanFoodDto implements Serializable {
        int id;
        String mainTitle ;
        String place;
        String subTitle;
        String img;
        String context;
        String tag1;
        String tag2;
        String tag3;
        double latitude;
        double longitude;
        //전화 string 추가
        String call;
        public int getId() {
            return id;
        }
    
        public void setId(int id) {
            this.id = id;
        }
    
        public String getMainTitle() {
            return mainTitle;
        }
        public void setMainTitle(String mainTitle) {
            this.mainTitle = mainTitle;
        }
        public String getPlace() {
            return place;
        }
        public void setPlace(String place) {
            this.place = place;
        }
        public String getSubTitle() {
            return subTitle;
        }
        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }
        public String getImg() {
            return img;
        }
        public void setImg(String img) {
            this.img = img;
        }
        public String getContext() {
            return context;
        }
        public void setContext(String context) {
            this.context = context;
        }
        public String getTag1() {
            return tag1;
        }
        public void setTag1(String tag1) {
            this.tag1 = tag1;
        }
        public String getTag2() {
            return tag2;
        }
        public void setTag2(String tag2) {
            this.tag2 = tag2;
        }
        public String getTag3() {
            return tag3;
        }
        public void setTag3(String tag3) {
            this.tag3 = tag3;
        }
        public double getLongitude() {
            return longitude;
        }
        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }
        public double getLatitude() {
            return latitude;
        }
        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    
        //call 추가
        public String getCall(){return call;}
        public void setCall(String call){this.call=call;}
    
    }
    ```

**Buttons Available in FoodFullImage:**
- Like button → add/remove from favorites list

- Call button → call the restaurant directly

- Share button → share Naver search link with friends

- Google Maps integration using latitude & longitude

**API Examples:**

- Add Like:
        
        ```js
        app.post('/likeFestivalHeart', (req, res) => {
            console.log("likeFestivalHeart 요청 시작 !!!!");
            console.log(req.body);
            var json_object_send = {
                code: ''
            };
        
            var sql = `insert into festival_like_mst values(?,?);`;
            var values = [req.body.userId, req.body.festivalId];
            console.log(req.body.userId);
            connection.query(sql, values, (error, rows) => {
                if (error) {
                    json_object_send.code = '0';
                    throw (error);
                }
                json_object_send.code = '1';
            });
            json_object_send.code = '1';
            console.log('Json object : ', json_object_send);
            res.send(json_object_send);
        });
        ```
        
        - Remove Like
        
        ```sql
        app.post('/dislikeFestivalHeart', (req, res) => {
            console.log("dislikeFestivalHeart 요청 시작 !!!!");
            console.log(req.body);
            var json_object_send = {
                code: ''
            };
            var sql = `delete from festival_like_mst where user_id = ? and festival = ?;`;
            var values = [req.body.userId, req.body.festivalId];
            console.log(req.body.userId);
            connection.query(sql, values, (error, rows) => {
                if (error) {
                    json_object_send.code = 0;
                    throw (error);
                }
                json_object_send.code = 1;
            });
            json_object_send.code = 1;
            console.log('Json object : ', json_object_send);
            res.send(json_object_send);
        });
        ```
        
- **Call button** → directly places a call to the restaurant.  
- **Share button** → shares the Naver search link of the restaurant with friends.  
- **Google Maps integration** → uses latitude and longitude values to display the restaurant’s location on the map.  
- **See More button** → redirects back to `BusanFoodStep1`, allowing the user to reload and view the list of places again. 

<br>

<br>

### 3. “어디 갈래?”(Where do you want to go?) : Attraction Recommendation

<div>

<image src="./app/src/main/res/drawable/s9.png" width="200"/>
<image src="./app/src/main/res/drawable/s10.png" width="200"/>
<image src="./app/src/main/res/drawable/s11.png" width="200"/>

</div>

- Same features as Restaurant Recommendation
- Except without the telephone function.

<br>

<br>

### “뭐 할래?”(What do you want to do?) : Festival Recommendation

<div>

<image src="./app/src/main/res/drawable/s12.png" width="200"/>
<image src="./app/src/main/res/drawable/s13.png" width="200"/>

</div>

- Displays all Busan festival information in a ListView (no tags).

- Each festival entry has a URL link to the official website.

### 5.“함 봐라”(Check it out) : Liked List

<div>

<image src="./app/src/main/res/drawable/s14.png" width="200"/>

<image src="./app/src/main/res/drawable/s15.png" width="200"/>
<image src="./app/src/main/res/drawable/s16.png" width="200"/>
<image src="./app/src/main/res/drawable/s17.png" width="200"/>

</div>

- Displays all liked items from:
    - 뭐 물래? (Restaurants)
    - 어디 갈래? (Attractions)
    - 뭐 할래? (Festival)
- Items are shown in a ListView.

- Clicking an item opens the FullImage Activity.

* * *
### Public Data Usage
- The app utilizes open public data to construct the following DTOs:

    - BusanFoodDto

    - BusanFestivalDto

    - BusanTodoDto
<image src="./app/src/main/res/drawable/sdata.png" width="200"/>
<image src="./app/src/main/res/drawable/sdata2.png" width="200"/>

---
