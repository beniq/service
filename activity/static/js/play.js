function Tiger1() {
    this.i = 0;
    this.x = 0;
    this.y = 0;
    this.w = 20;
    this.h = 20;
    this.max = 0;
    this.onFinish = null
    this.element = null;
    this.pos = [[0, 0], [0, 1], [0, 2], [1, 2], [2, 2], [2, 1], [2, 0], [1, 0]];
};
Tiger1.prototype.go = function(d, l, of, x, y, w, h) {
    if (this.i < this.max)
        return;
    this.element = d;
    this.i = 0;
    this.max = l;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.onFinish = of;
    var _this = this;
    this.run = function() {
        var xy = _this.pos[_this.i % 8];
        var e = $("#" + _this.element);
        pot(_this.element, _this.x + xy[0] * _this.w, _this.y + xy[1] * _this.h);
        if (_this.i == 0)
            e.show();
        _this.i++;
        if (_this.i < _this.max) {
            var t = 50;
            if (_this.i < 20)
                t = (29 - _this.i) * 5;
            else if (_this.i >= _this.max - 20)
                t = Math.pow(_this.i - _this.max + 21, 2) + 50;
            setTimeout(_this.run.bind(_this), t);
        } else {
            if (_this.onFinish != null)
                _this.onFinish();
        }
    };
    setTimeout(this.run, 200);
};

function star1(div, num, w, h, size){
    for(var ii = 0; ii < num; ii++){
        var xx = Math.floor(Math.random()*w/3)+(ii%3)*w/3;
        var yy = Math.floor(Math.random()*h/3)+Math.floor((ii%9)/3)*h/3;
        var ww = Math.floor(Math.random()*size+size/2);
        pot(div+"_"+ii , xx - ww/2, yy - ww/2,  ww, ww);
        $("#"+div+"_"+ii).attr("src", "https://static.iyunbao.com/website/health/iyb/resource/activity/gpo/images/timemachine/xingxing_"+Math.floor(Math.random()*3+1)+".png");
    }
}


function Sparks(canvas) {
    this.canvas = canvas;
    this.user={x:0, y:0, z:-25};
    this.pitch=0;
    this.yaw=0;
    this.scale=600;
    this.seedTimer=0;
    this.seedInterval=5;
    this.seedLife=100;
    this.gravity=0.02;
    this.sparkPics=new Array();
    this.url="../../images/";
    for(i=1;i<=10;++i){
        var sparkPic=new Image();
        sparkPic.src=this.url+"spark"+i+".png";
        this.sparkPics.push(sparkPic);
    }
    this.maxFrame = 60000;
    this.pow1=new Audio(this.url+"pow1.ogg");
    this.pow2=new Audio(this.url+"pow2.ogg");
    this.pow3=new Audio(this.url+"pow3.ogg");
    this.pow4=new Audio(this.url+"pow4.ogg");
    this.frames = 0;
    this.ctx = canvas.getContext("2d");;
    this.seeds = [];
    this.sparks = [];
    this.cx = canvas.width / 2;
    this.cy = canvas.height / 2;
    Sparks.prototype.spawnSeed = function() {
        var seed=new Object();
        seed.x=-50+Math.random()*100;
        seed.y=25;
        seed.z=-50+Math.random()*100;
        seed.vx=.1-Math.random()*.2;
        seed.vy=-1.25-Math.random();
        seed.vz=.1-Math.random()*.2;
        seed.born=ENV.sparksThis.frames;
        ENV.sparksThis.seeds.push(seed);
    };
    Sparks.prototype.rasterizePoint = function(x,y,z) {
        var p,d;
        x-=ENV.sparksThis.user.x;
        y-=ENV.sparksThis.user.y;
        z-=ENV.sparksThis.user.z;
        p=Math.atan2(x,z);
        d=Math.sqrt(x*x+z*z);
        x=Math.sin(p-ENV.sparksThis.yaw)*d;
        z=Math.cos(p-ENV.sparksThis.yaw)*d;
        p=Math.atan2(y,z);
        d=Math.sqrt(y*y+z*z);
        y=Math.sin(p-ENV.sparksThis.pitch)*d;
        z=Math.cos(p-ENV.sparksThis.pitch)*d;
        var rx1=-1000,ry1=1,rx2=1000,ry2=1,rx3=0,ry3=0,rx4=x,ry4=z,uc=(ry4-ry3)*(rx2-rx1)-(rx4-rx3)*(ry2-ry1);
        if(!uc) return {x:0,y:0,d:-1};
        var ua=((rx4-rx3)*(ry1-ry3)-(ry4-ry3)*(rx1-rx3))/uc;
        var ub=((rx2-rx1)*(ry1-ry3)-(ry2-ry1)*(rx1-rx3))/uc;
        if(!z)z=.000000001;
        if(ua>0&&ua<1&&ub>0&&ub<1){
            return {
                x:ENV.sparksThis.cx+(rx1+ua*(rx2-rx1))*ENV.sparksThis.scale,
                y:ENV.sparksThis.cy+y/z*ENV.sparksThis.scale,
                d:Math.sqrt(x*x+y*y+z*z)
            };
        }else{
            return {
                x:ENV.sparksThis.cx+(rx1+ua*(rx2-rx1))*ENV.sparksThis.scale,
                y:ENV.sparksThis.cy+y/z*ENV.sparksThis.scale,
                d:-1
            };
        }
    };
    Sparks.prototype.splode = function(x,y,z) {
        var t=5+parseInt(Math.random()*150);
        sparkV=1+Math.random()*2.5;
        type=parseInt(Math.random()*3);
        var pic1, pic2, pic3;
        switch(type){
            case 0:
                pic1=parseInt(Math.random()*10);
                break;
            case 1:
                pic1=parseInt(Math.random()*10);
                do{ pic2=parseInt(Math.random()*10); }while(pic2==pic1);
                break;
            case 2:
                pic1=parseInt(Math.random()*10);
                do{ pic2=parseInt(Math.random()*10); }while(pic2==pic1);
                do{ pic3=parseInt(Math.random()*10); }while(pic3==pic1 || pic3==pic2);
                break;
        }
        for(m=1;m<t;++m){
            var spark=new Object();
            spark.x=x; spark.y=y; spark.z=z;
            var p1=Math.PI*2*Math.random();
            var p2=Math.PI*Math.random();
            v=sparkV*(1+Math.random()/6)
            spark.vx=Math.sin(p1)*Math.sin(p2)*v;
            spark.vz=Math.cos(p1)*Math.sin(p2)*v;
            spark.vy=Math.cos(p2)*v;
            switch(type){
                case 0: spark.img=ENV.sparksThis.sparkPics[pic1]; break;
                case 1:
                    spark.img=ENV.sparksThis.sparkPics[parseInt(Math.random()*2)?pic1:pic2];
                    break;
                case 2:
                    switch(parseInt(Math.random()*3)){
                        case 0: spark.img=ENV.sparksThis.sparkPics[pic1]; break;
                        case 1: spark.img=ENV.sparksThis.sparkPics[pic2]; break;
                        case 2: spark.img=ENV.sparksThis.sparkPics[pic3]; break;
                    }
                    break;
            }
            spark.radius=25+Math.random()*50;
            spark.alpha=1;
            spark.trail=new Array();
            ENV.sparksThis.sparks.push(spark);
        }
        var pow;
        switch(parseInt(Math.random()*4)){
            case 0:	pow=new Audio(ENV.sparksThis.url+"pow1.ogg"); break;
            case 1:	pow=new Audio(ENV.sparksThis.url+"pow2.ogg"); break;
            case 2:	pow=new Audio(ENV.sparksThis.url+"pow3.ogg"); break;
            case 3:	pow=new Audio(ENV.sparksThis.url+"pow4.ogg"); break;
        }
        var d=Math.sqrt((x-ENV.sparksThis.user.x)*(x-ENV.sparksThis.user.x)+(y-ENV.sparksThis.user.y)*(y-ENV.sparksThis.user.y)+(z-ENV.sparksThis.user.z)*(z-ENV.sparksThis.user.z));
        pow.volume=1.5/(1+d/10);
        pow.play();
    };
    Sparks.prototype.doLogic = function() {
        if(ENV.sparksThis.seedTimer<ENV.sparksThis.frames && ENV.sparksThis.frames<ENV.sparksThis.maxFrame-ENV.sparksThis.seedLife*2){
            ENV.sparksThis.seedTimer=ENV.sparksThis.frames+ENV.sparksThis.seedInterval*Math.random()*10;
            ENV.sparksThis.spawnSeed();
        }
        var seeds = ENV.sparksThis.seeds;
        for(var i=0;i<seeds.length;++i){
            seeds[i].vy+=ENV.sparksThis.gravity;
            seeds[i].x+=seeds[i].vx;
            seeds[i].y+=seeds[i].vy;
            seeds[i].z+=seeds[i].vz;
            if(ENV.sparksThis.frames-seeds[i].born>ENV.sparksThis.seedLife){
                ENV.sparksThis.splode(seeds[i].x,seeds[i].y,seeds[i].z);
                seeds.splice(i,1);
            }
        }
        var sparks = ENV.sparksThis.sparks;
        for(var i=0;i<sparks.length;++i){
            if(sparks[i].alpha>0 && sparks[i].radius>5){
                sparks[i].alpha-=.01;
                sparks[i].radius/=1.02;
                sparks[i].vy+=ENV.sparksThis.gravity;
                var point=new Object();
                point.x=sparks[i].x;
                point.y=sparks[i].y;
                point.z=sparks[i].z;
                if(sparks[i].trail.length){
                    x=sparks[i].trail[sparks[i].trail.length-1].x;
                    y=sparks[i].trail[sparks[i].trail.length-1].y;
                    z=sparks[i].trail[sparks[i].trail.length-1].z;
                    var d=((point.x-x)*(point.x-x)+(point.y-y)*(point.y-y)+(point.z-z)*(point.z-z));
                    if(d>9){
                        sparks[i].trail.push(point);
                    }
                }else{
                    sparks[i].trail.push(point);
                }
                if(sparks[i].trail.length>5)sparks[i].trail.splice(0,1);
                sparks[i].x+=sparks[i].vx;
                sparks[i].y+=sparks[i].vy;
                sparks[i].z+=sparks[i].vz;
                sparks[i].vx/=1.075;
                sparks[i].vy/=1.075;
                sparks[i].vz/=1.075;
            }else{
                sparks.splice(i,1);
            }
        }
        // var p=Math.atan2(ENV.sparksThis.user.x,ENV.sparksThis.user.z);
        // var d=Math.sqrt(ENV.sparksThis.user.x*ENV.sparksThis.user.x+ENV.sparksThis.user.z*ENV.sparksThis.user.z);
        // d+=Math.sin(ENV.sparksThis.frames/80)/1.25;
        // var t=Math.sin(ENV.sparksThis.frames/200)/40;
        var p=0, t=0, d=100;
        ENV.sparksThis.user.x=Math.sin(p+t)*d;
        ENV.sparksThis.user.z=Math.cos(p+t)*d;
        ENV.sparksThis.yaw=Math.PI+p+t;
    };
    Sparks.prototype.rgb = function(col) {
        var r = parseInt((.5+Math.sin(col)*.5)*16);
        var g = parseInt((.5+Math.cos(col)*.5)*16);
        var b = parseInt((.5-Math.sin(col)*.5)*16);
        return "#"+r.toString(16)+g.toString(16)+b.toString(16);
    };
    Sparks.prototype.draw = function() {
        ENV.sparksThis.ctx.clearRect(0,0,ENV.sparksThis.cx*2,ENV.sparksThis.cy*2);
        ENV.sparksThis.ctx.globalAlpha=1;
        var seeds = ENV.sparksThis.seeds;
        var sparks = ENV.sparksThis.sparks;
        for(var i=0;i<seeds.length;++i){
            var point=ENV.sparksThis.rasterizePoint(seeds[i].x,seeds[i].y,seeds[i].z);
            if(point.d!=-1){
                var size=200/(1+point.d);
                ENV.sparksThis.ctx.drawImage(ENV.sparksThis.sparkPics[0],point.x-size*4,point.y-size*4,size*8,size*8);
                //ENV.sparksThis.ctx.fillRect(point.x-size/2,point.y-size/2,size,size);
            }
        }
        var point1=new Object();
        for(var i=0;i<sparks.length;++i){
            var point=ENV.sparksThis.rasterizePoint(sparks[i].x,sparks[i].y,sparks[i].z);
            if(point.d!=-1){
                var size=sparks[i].radius*200/(1+point.d);
                if(sparks[i].alpha<0)sparks[i].alpha=0;
                if(sparks[i].trail.length){
                    point1.x=point.x;
                    point1.y=point.y;
                    switch(sparks[i].img){
                        case ENV.sparksThis.sparkPics[0]:ENV.sparksThis.ctx.strokeStyle="#f84";break;
                        case ENV.sparksThis.sparkPics[1]:ENV.sparksThis.ctx.strokeStyle="#84f";break;
                        case ENV.sparksThis.sparkPics[2]:ENV.sparksThis.ctx.strokeStyle="#8ff";break;
                        case ENV.sparksThis.sparkPics[3]:ENV.sparksThis.ctx.strokeStyle="#fff";break;
                        case ENV.sparksThis.sparkPics[4]:ENV.sparksThis.ctx.strokeStyle="#4f8";break;
                        case ENV.sparksThis.sparkPics[5]:ENV.sparksThis.ctx.strokeStyle="#f44";break;
                        case ENV.sparksThis.sparkPics[6]:ENV.sparksThis.ctx.strokeStyle="#f84";break;
                        case ENV.sparksThis.sparkPics[7]:ENV.sparksThis.ctx.strokeStyle="#84f";break;
                        case ENV.sparksThis.sparkPics[8]:ENV.sparksThis.ctx.strokeStyle="#fff";break;
                        case ENV.sparksThis.sparkPics[9]:ENV.sparksThis.ctx.strokeStyle="#44f";break;
                    }
                    for(j=sparks[i].trail.length-1;j>=0;--j){
                        point2=ENV.sparksThis.rasterizePoint(sparks[i].trail[j].x,sparks[i].trail[j].y,sparks[i].trail[j].z);
                        if(point2.d!=-1){
                            ENV.sparksThis.ctx.globalAlpha=j/sparks[i].trail.length*sparks[i].alpha/2;
                            ENV.sparksThis.ctx.beginPath();
                            ENV.sparksThis.ctx.moveTo(point1.x,point1.y);
                            ENV.sparksThis.ctx.lineWidth=1+sparks[i].radius*10/(sparks[i].trail.length-j)/(1+point2.d);
                            ENV.sparksThis.ctx.lineTo(point2.x,point2.y);
                            ENV.sparksThis.ctx.stroke();
                            point1.x=point2.x;
                            point1.y=point2.y;
                        }
                    }
                }
                ENV.sparksThis.ctx.globalAlpha=1;//sparks[i].alpha;
                if (Math.random() + sparks[i].alpha > 1)
                    ENV.sparksThis.ctx.drawImage(sparks[i].img,point.x-size/2,point.y-size/2,size,size);
            }
        }
    };
    Sparks.prototype.frame = function() {
        if (ENV.sparksThis.frames < 0 || ENV.sparksThis.frames > ENV.sparksThis.maxFrame){
            return;
        }
        if (!ENV.sparksThis.isVisible(ENV.sparksThis.canvas)) return;
        ENV.sparksThis.frames++;
        ENV.sparksThis.draw();
        ENV.sparksThis.doLogic();
        requestAnimationFrame(ENV.sparksThis.frame);
    };
    Sparks.prototype.stop = function() {
        ENV.sparksThis.frames = -100;
    };
    Sparks.prototype.start = function() {
        ENV.sparksThis = this;
        for (ENV.sparksThis.frames = 0;ENV.sparksThis.frames < 80;ENV.sparksThis.frames++)
            ENV.sparksThis.doLogic();
        ENV.sparksThis.frame();
    };
    Sparks.prototype.isVisible = function(e) {
        if (e == null || e.style == null)
            return true;
        if (e.style.display == "none")
            return false;
        return e.parentNode ? ENV.sparksThis.isVisible(e.parentNode) : true;
    };
}