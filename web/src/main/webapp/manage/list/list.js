	//Model
	var data = {
	    heros: [
	    { id: 1, name: '盖伦', hp: 318},
	    { id: 2, name: '提莫', hp: 320},
	    { id: 3, name: '安妮', hp: 419},
	    { id: 4, name: '死歌', hp: 325},
	    { id: 5, name: '米波', hp: 422},
	    ],
	    hero4Add: { id: 0, name: '', hp: '0'},
	    hero4Update: { id: 0, name: '', hp: '0'}
	};
        
    //ViewModel
    var vue = new Vue({
        el: '#app',
        data: data
    });
