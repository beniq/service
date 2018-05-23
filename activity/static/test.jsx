function Sparks(canvas) {
    this.canvas = canvas;
    this.user={x:0, y:0, z:-25};
    this.pitch=this.yaw;
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