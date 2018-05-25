var ENV = {
    H: 1200,
    init: {}
};
var zoom = function(){
    var xx = document.getElementById("ccc");
    return xx.offsetWidth / 750;
};
var zoomY = function(){
    var xx = document.getElementById("ccc");
    return xx.offsetHeight / ENV.H;
};
var pot = function(name, x, y, w, h, tp)
{
    var rh = h == -1 ? window.innerHeight : zoomY() * h;
    var d5 = document.getElementById(name);
    d5.style.width = zoom() * w;
    d5.style.height = rh;
    d5.style.position = tp ? tp : "absolute";
    d5.style.left = zoom() * x;
    d5.setAttribute("h", rh);
    if (y == -10000) {
        d5.style.top = (Number(d5.parentNode.getAttribute("h")) - rh) / 2;
    } else if (y == -20000) {
        if (tp == "fixed") {
            d5.style.top = window.innerHeight - rh;
        } else {
            d5.style.top = (Number(d5.parentNode.getAttribute("h")) - rh);
        }
    } else {
        d5.style.top = zoomY() * y;
    }
};
var fnt = function(name, size1, size2)
{
    try{
        var obj = document.getElementById(name);
        obj.style.fontSize = (zoom() * size1)+'px';
        obj.style.lineHeight = (zoom() * size2)+'px';
    }catch(e){}
};
var hidedown = function(e)
{
    e.stopPropagation();
    e.preventDefault();
};


(function (root, factory) {
    if (typeof define === 'function' && define.amd) {
        //AMD
        define(factory);
    } else if (typeof exports === 'object') {
        //Node, CommonJS之类的
        module.exports = factory();
    } else {
        //浏览器全局变量(root 即 window)
        root.resLoader = factory(root);
    }
}(this, function () {
    var isFunc = function (f) {
        return typeof f === 'function';
    };
    //构造器函数
    function resLoader(config) {
        this.option = {
            resourceType: 'image', //资源类型，默认为图片
            baseUrl: './', //基准url
            resources: [], //资源路径数组
            onStart: null, //加载开始回调函数，传入参数total
            onProgress: null, //正在加载回调函数，传入参数currentIndex, total
            onComplete: null //加载完毕回调函数，传入参数total
        };
        if (!!config) {
            for (i in config) {
                this.option[i] = config[i];
            }
        }
        else {
            console.log('参数错误！');
            return;
        }
        this.status = 0; //加载器的状态，0：未启动   1：正在加载   2：加载完毕
        this.total = this.option.resources.length || 0; //资源总数
        this.currentIndex = 0; //当前正在加载的资源索引
    };

    resLoader.prototype.start = function () {
        this.status = 1;
        var _this = this;
        var baseUrl = this.option.baseUrl;
        for (var i = 0, l = this.option.resources.length; i < l; i++) {
            var r = this.option.resources[i], url = '';
            if (r.indexOf('http://') === 0 || r.indexOf('https://') === 0) {
                url = r;
            }
            else {
                url = baseUrl + r;
            }

            var image = new Image();
            image.onload = function () {
                _this.loaded();
            };
            image.onerror = function () {
                _this.loaded();
            };
            image.src = url;
        }
        if (isFunc(this.option.onStart)) {
            this.option.onStart(this.total);
        }
    };

    resLoader.prototype.loaded = function () {
        if (isFunc(this.option.onProgress)) {
            this.option.onProgress(++this.currentIndex, this.total);
        }
        //加载完毕
        if (this.currentIndex === this.total && this.status != 2) {
            if (isFunc(this.option.onComplete)) {
                this.option.onComplete(this.total);
            }
            this.status = 2;
        }
    };

    //暴露公共方法
    return resLoader;
}));



function Tiger1() {
	this.i = 0;
    this.x = 0;
    this.y = 0;
    this.w = 20;
    this.h = 20;
	this.max = 0;
	this.onFinish = null
	this.element = null;
	this.pos = [[0,0],[0,1],[0,2],[1,2],[2,2],[2,1],[2,0],[1,0]];
    this.go = (d, l, of, x, y, w, h) => {
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
        setTimeout(this.run, 200);
    };
    this.run = () => {
        let xy = this.pos[this.i % 8];
        let e = $("#" + this.element);
        pot(this.element, this.x + xy[0] * this.w, this.y + xy[1] * this.h);
        if (this.i == 0)
            e.show();
        this.i++;
        if (this.i < this.max) {
        	let t = 50;
        	if (this.i < 20)
        		t = (29 - this.i) * 5;
        	else if (this.i >= this.max - 20)
            	t = Math.pow(this.i - this.max + 21, 2) + 50;
            setTimeout(this.run, t);
        } else {
            if (this.onFinish != null)
                this.onFinish();
        }
    };
};


function star1(div, num, w, h, size){
    for(let ii = 0; ii < num; ii++){
        let xx = Math.floor(Math.random()*w/3)+(ii%3)*w/3;
        let yy = Math.floor(Math.random()*h/3)+Math.floor((ii%9)/3)*h/3;
        let ww = Math.floor(Math.random()*size+size/2);
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
        let sparkPic=new Image();
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
    this.spawnSeed = () => {
        let seed=new Object();
        seed.x=-50+Math.random()*100;
        seed.y=25;
        seed.z=-50+Math.random()*100;
        seed.vx=.1-Math.random()*.2;
        seed.vy=-1.25-Math.random();
        seed.vz=.1-Math.random()*.2;
        seed.born=this.frames;
        this.seeds.push(seed);
    };
    this.rasterizePoint = (x,y,z) => {
        let p,d;
        x-=this.user.x;
        y-=this.user.y;
        z-=this.user.z;
        p=Math.atan2(x,z);
        d=Math.sqrt(x*x+z*z);
        x=Math.sin(p-this.yaw)*d;
        z=Math.cos(p-this.yaw)*d;
        p=Math.atan2(y,z);
        d=Math.sqrt(y*y+z*z);
        y=Math.sin(p-this.pitch)*d;
        z=Math.cos(p-this.pitch)*d;
        let rx1=-1000,ry1=1,rx2=1000,ry2=1,rx3=0,ry3=0,rx4=x,ry4=z,uc=(ry4-ry3)*(rx2-rx1)-(rx4-rx3)*(ry2-ry1);
        if(!uc) return {x:0,y:0,d:-1};
        let ua=((rx4-rx3)*(ry1-ry3)-(ry4-ry3)*(rx1-rx3))/uc;
        let ub=((rx2-rx1)*(ry1-ry3)-(ry2-ry1)*(rx1-rx3))/uc;
        if(!z)z=.000000001;
        if(ua>0&&ua<1&&ub>0&&ub<1){
            return {
                x:this.cx+(rx1+ua*(rx2-rx1))*this.scale,
                y:this.cy+y/z*this.scale,
                d:Math.sqrt(x*x+y*y+z*z)
            };
        }else{
            return {
                x:this.cx+(rx1+ua*(rx2-rx1))*this.scale,
                y:this.cy+y/z*this.scale,
                d:-1
            };
        }
    };
    this.splode = (x,y,z) => {
        let t=5+parseInt(Math.random()*150);
        sparkV=1+Math.random()*2.5;
        type=parseInt(Math.random()*3);
        let pic1, pic2, pic3;
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
            let spark=new Object();
            spark.x=x; spark.y=y; spark.z=z;
            let p1=Math.PI*2*Math.random();
            let p2=Math.PI*Math.random();
            v=sparkV*(1+Math.random()/6)
            spark.vx=Math.sin(p1)*Math.sin(p2)*v;
            spark.vz=Math.cos(p1)*Math.sin(p2)*v;
            spark.vy=Math.cos(p2)*v;
            switch(type){
                case 0: spark.img=this.sparkPics[pic1]; break;
                case 1:
                    spark.img=this.sparkPics[parseInt(Math.random()*2)?pic1:pic2];
                    break;
                case 2:
                    switch(parseInt(Math.random()*3)){
                        case 0: spark.img=this.sparkPics[pic1]; break;
                        case 1: spark.img=this.sparkPics[pic2]; break;
                        case 2: spark.img=this.sparkPics[pic3]; break;
                    }
                    break;
            }
            spark.radius=25+Math.random()*50;
            spark.alpha=1;
            spark.trail=new Array();
            this.sparks.push(spark);
        }
        let pow;
        switch(parseInt(Math.random()*4)){
            case 0:	pow=new Audio(this.url+"pow1.ogg"); break;
            case 1:	pow=new Audio(this.url+"pow2.ogg"); break;
            case 2:	pow=new Audio(this.url+"pow3.ogg"); break;
            case 3:	pow=new Audio(this.url+"pow4.ogg"); break;
        }
        let d=Math.sqrt((x-this.user.x)*(x-this.user.x)+(y-this.user.y)*(y-this.user.y)+(z-this.user.z)*(z-this.user.z));
        pow.volume=1.5/(1+d/10);
        pow.play();
    };
    this.doLogic = () => {
        if(this.seedTimer<this.frames && this.frames<this.maxFrame-this.seedLife*2){
            this.seedTimer=this.frames+this.seedInterval*Math.random()*10;
            this.spawnSeed();
        }
        let seeds = this.seeds;
        for(let i=0;i<seeds.length;++i){
            seeds[i].vy+=this.gravity;
            seeds[i].x+=seeds[i].vx;
            seeds[i].y+=seeds[i].vy;
            seeds[i].z+=seeds[i].vz;
            if(this.frames-seeds[i].born>this.seedLife){
                this.splode(seeds[i].x,seeds[i].y,seeds[i].z);
                seeds.splice(i,1);
            }
        }
        let sparks = this.sparks;
        for(let i=0;i<sparks.length;++i){
            if(sparks[i].alpha>0 && sparks[i].radius>5){
                sparks[i].alpha-=.01;
                sparks[i].radius/=1.02;
                sparks[i].vy+=this.gravity;
                let point=new Object();
                point.x=sparks[i].x;
                point.y=sparks[i].y;
                point.z=sparks[i].z;
                if(sparks[i].trail.length){
                    x=sparks[i].trail[sparks[i].trail.length-1].x;
                    y=sparks[i].trail[sparks[i].trail.length-1].y;
                    z=sparks[i].trail[sparks[i].trail.length-1].z;
                    let d=((point.x-x)*(point.x-x)+(point.y-y)*(point.y-y)+(point.z-z)*(point.z-z));
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
        // let p=Math.atan2(this.user.x,this.user.z);
        // let d=Math.sqrt(this.user.x*this.user.x+this.user.z*this.user.z);
        // d+=Math.sin(this.frames/80)/1.25;
        // let t=Math.sin(this.frames/200)/40;
        let p=0, t=0, d=100;
        this.user.x=Math.sin(p+t)*d;
        this.user.z=Math.cos(p+t)*d;
        this.yaw=Math.PI+p+t;
    };
    this.rgb = (col) => {
        let r = parseInt((.5+Math.sin(col)*.5)*16);
        let g = parseInt((.5+Math.cos(col)*.5)*16);
        let b = parseInt((.5-Math.sin(col)*.5)*16);
        return "#"+r.toString(16)+g.toString(16)+b.toString(16);
    };
    this.draw = () => {
        this.ctx.clearRect(0,0,this.cx*2,this.cy*2);
        this.ctx.globalAlpha=1;
        let seeds = this.seeds;
        for(let i=0;i<seeds.length;++i){
            let point=this.rasterizePoint(seeds[i].x,seeds[i].y,seeds[i].z);
            if(point.d!=-1){
                let size=200/(1+point.d);
                this.ctx.fillRect(point.x-size/2,point.y-size/2,size,size);
            }
        }
        let point1=new Object();
        let sparks = this.sparks;
        for(let i=0;i<sparks.length;++i){
            let point=this.rasterizePoint(sparks[i].x,sparks[i].y,sparks[i].z);
            if(point.d!=-1){
                let size=sparks[i].radius*200/(1+point.d);
                if(sparks[i].alpha<0)sparks[i].alpha=0;
                if(sparks[i].trail.length){
                    point1.x=point.x;
                    point1.y=point.y;
                    switch(sparks[i].img){
                        case this.sparkPics[0]:this.ctx.strokeStyle="#f84";break;
                        case this.sparkPics[1]:this.ctx.strokeStyle="#84f";break;
                        case this.sparkPics[2]:this.ctx.strokeStyle="#8ff";break;
                        case this.sparkPics[3]:this.ctx.strokeStyle="#fff";break;
                        case this.sparkPics[4]:this.ctx.strokeStyle="#4f8";break;
                        case this.sparkPics[5]:this.ctx.strokeStyle="#f44";break;
                        case this.sparkPics[6]:this.ctx.strokeStyle="#f84";break;
                        case this.sparkPics[7]:this.ctx.strokeStyle="#84f";break;
                        case this.sparkPics[8]:this.ctx.strokeStyle="#fff";break;
                        case this.sparkPics[9]:this.ctx.strokeStyle="#44f";break;
                    }
                    for(j=sparks[i].trail.length-1;j>=0;--j){
                        point2=this.rasterizePoint(sparks[i].trail[j].x,sparks[i].trail[j].y,sparks[i].trail[j].z);
                        if(point2.d!=-1){
                            this.ctx.globalAlpha=j/sparks[i].trail.length*sparks[i].alpha/2;
                            this.ctx.beginPath();
                            this.ctx.moveTo(point1.x,point1.y);
                            this.ctx.lineWidth=1+sparks[i].radius*10/(sparks[i].trail.length-j)/(1+point2.d);
                            this.ctx.lineTo(point2.x,point2.y);
                            this.ctx.stroke();
                            point1.x=point2.x;
                            point1.y=point2.y;
                        }
                    }
                }
                this.ctx.globalAlpha=1;//sparks[i].alpha;
                if (Math.random() + sparks[i].alpha > 1)
                    this.ctx.drawImage(sparks[i].img,point.x-size/2,point.y-size/2,size,size);
            }
        }
    };
    this.frame = () => {
        if (this.frames < 0 || this.frames > this.maxFrame){
            return;
        }
        if (!this.isVisible(this.canvas))
            return;
        this.frames++;
        this.draw();
        this.doLogic();
        requestAnimationFrame(this.frame);
    };
    this.stop = () => {
        this.frames = -100;
    };
    this.start = () => {
        for (this.frames = 0;this.frames < 80;this.frames++)
            this.doLogic();
        this.frame();
    };
    this.isVisible = (e) => {
        if (e == null || e.style == null)
            return true;
        if (e.style.display == "none")
            return false;
        return e.parentNode ? this.isVisible(e.parentNode) : true;
    };
}