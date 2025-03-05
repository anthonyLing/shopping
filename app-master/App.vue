<script>
	import { HTTP_REQUEST_URL } from './config/app';
	import { loginConfigApi } from '@/api/public';
	import Auth from './libs/wechat.js';
	import Routine from './libs/routine.js';
	import {
		getTheme
	} from './api/api.js';
	import {
		mapActions
	} from 'vuex'
	import {
		spread
	} from "@/api/user";
	import Cache from './utils/cache.js';
	import store from './store'
	var statusBarHeight = uni.getSystemInfoSync().statusBarHeight; //手机端头部手机时间位置高度
	export default {
		globalData: {
			spread: 0, //推广人id，绑定关系
			isLogin: false,
			windowHeight: 0,
			navHeight: 0, //导航高度
			navH: 0,
			id: 0, // 小程序扫码进入商品id，首页id
			isIframe: false,
			theme: Cache.get('theme') ? Cache.get('theme') : 'theme1',
			statusBarHeight: statusBarHeight, //手机端头部手机时间位置高度
			mainWidth: 0 ,//获取设备宽度
			type: '',//商品类型，normal普通商品
			publicLoginType: Cache.get('publicLoginType')?Cache.get('publicLoginType'):'', //公众号登录方式(单选),1微信授权，2手机号登录
			routinePhoneVerification: '1,2' ,//小程序手机号校验类型（多选）1微信小程序验证 2短信验证
			companyName: uni.getStorageSync('companyName')?uni.getStorageSync('companyName'):'欢迎你',//公司名称
			tokenIsExist: false, //登录是否失效 false 失效，true没失效
			mobileLoginLogo: uni.getStorageSync('mobileLoginLogo') || '/static/images/logo2.png'//登录页logo
		},
		onLaunch: function(option) {
			let that = this;
			
			//获取登录配置
			that.getLoginConfig();
			
			//校验token是否有效,true为有效，false为无效
			that.$LoginAuth.getTokenIsExist().then(data => {
				that.globalData.tokenIsExist = data;
			});
			
			// 主题变色
			getTheme().then(resP => {
				Cache.set('theme', `theme${Number(resP.data.value)}`);
				that.globalData.theme = `theme${Number(resP.data.value)}`;
				// #ifdef H5
				window.document.documentElement.setAttribute('data-theme', that.globalData.theme);
				// #endif
			})	
			
			// 获取页面高度
			uni.getSystemInfo({
				success: function(res) {
					// 首页没有title获取的整个页面的高度，里面的页面有原生标题要减掉就是视口的高度  
					// 状态栏是动态的可以拿到 标题栏是固定写死的是44px
					let height = res.windowHeight - res.statusBarHeight - 44;
					// #ifdef H5 || APP-PLUS
					that.globalData.windowHeight = res.windowHeight + 'px';
					// #endif
					// 获取导航高度；
					that.globalData.navHeight = res.statusBarHeight * (750 / res.windowWidth) + 91;
					// 获取设备宽度；
					that.globalData.mainWidth = res.windowWidth
					store.commit('SYSTEM_PLATFORM', res.platform.toLowerCase());
				}
			});
			
			// 获取浏览器推广人id
			if (option.query.spread) {
				that.globalData.spread = option.query.spread;
				store.commit('GLOBAL_DATA', JSON.parse(JSON.stringify(that.globalData)));
			}
			if (option.spread) {
				that.globalData.spread = option.spread;
				store.commit('GLOBAL_DATA', JSON.parse(JSON.stringify(that.globalData)));
			}
			
			// #ifdef MP
			//小程序扫码进入获取浏览器值
			if (option.query.hasOwnProperty('scene')) {
				switch (option.scene) {
					case 1001: //直接进入小程序
					case 1047: //扫描小程序码
					case 1048: //长按图片识别小程序码
					case 1049: //手机相册选取小程序码
					case 1001: //直接进入小程序
						let value = this.$util.getUrlParams(decodeURIComponent(option.query.scene));
						that.globalData.spread = value.spread?value.spread:'';
						that.globalData.id = value.id?value.id:'';
						that.globalData.type = value.type?value.type:'normal';
						break;
				}
			}
			let menuButtonInfo = uni.getMenuButtonBoundingClientRect();
			that.globalData.navH = menuButtonInfo.top * 2 + menuButtonInfo.height / 2;
			const updateManager = uni.getUpdateManager();
			updateManager.onCheckForUpdate(function(res) {
				// 请求完新版本信息的回调
				if (res.hasUpdate) {
					updateManager.onUpdateReady(function(res2) {
						uni.showModal({
							title: '更新提示',
							content: '发现新版本，是否重启应用?',
							cancelColor: '#eeeeee',
							confirmColor: '#FF0000',
							success(res2) {
								if (res2.confirm) {
									// 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
									updateManager.applyUpdate();
								}
							}
						});
					});
				}
			});
			updateManager.onUpdateFailed(function(res) {
				// 新的版本下载失败
				uni.showModal({
					title: '提示',
					content: '检查到有新版本，但下载失败，请检查网络设置',
					success(res) {
						if (res.confirm) {
							// 新的版本已经下载好，调用 applyUpdate 应用新版本并重启
							updateManager.applyUpdate();
						}
					}
				});
			});
			if (HTTP_REQUEST_URL == '') {
				console.error(
					"请配置根目录下的config.js文件中的 'HTTP_REQUEST_URL'\n\n请修改开发者工具中【详情】->【AppID】改为自己的Appid\n\n请前往后台【小程序】->【小程序配置】填写自己的 appId and AppSecret"
				);
				return false;
			}
			// #endif
			
			// #ifdef H5	
			if (option.query.hasOwnProperty('type') && option.query.type == "iframeVisualizing") {
				this.globalData.isIframe = true;
			} else {
				this.globalData.isIframe = false;
			}
			
            // wx公众号自动授权登录
			let snsapiBase = 'snsapi_base';
			let urlData = location.pathname + location.search;
			//publicLoginType，公众号登录方式(单选),1微信授权，2手机号登录
			if (!store.getters.isLogin && Auth.isWeixin() && this.globalData.publicLoginType ==1 && !that.globalData.tokenIsExist) {
				const {
					code,
					state,
					scope
				} = option.query;
				if (code && code != uni.getStorageSync('snsapiCode') && location.pathname.indexOf(
						'/pages/users/wechat_login/index') === -1) {
					// 存储静默授权code
					uni.setStorageSync('snsapiCode', code);
					let spread = that.globalData.spread ? that.globalData.spread : 0;
					Auth.auth(code, Cache.get('spread'))
						.then(res => {
							uni.setStorageSync('snRouter', decodeURIComponent(decodeURIComponent(option.query
								.back_url)));
							if (res.type === 'register') {
								Cache.set('snsapiKey', res.key);
							}
							if (res.type === 'login') {
								store.commit('LOGIN', {
									token: res.token
								});
								store.commit("SETUID", res.id);
								location.replace(decodeURIComponent(decodeURIComponent(option.query.back_url)));
							}
						})
						.catch(error => {
							if (!Cache.has('snsapiKey')) {
								if (location.pathname.indexOf('/pages/users/wechat_login/index') === -1) {
									Auth.oAuth(snsapiBase, option.query.back_url);
								}
							}
						});
				} else {
					if (!Cache.has('snsapiKey')) {
						if (location.pathname.indexOf('/pages/users/wechat_login/index') === -1) {
							Auth.oAuth(snsapiBase, urlData);
						}
					}
				}
			} else {
				if (option.query.back_url) {
					location.replace(uni.getStorageSync('snRouter'));
				}
			}
			// #endif

			// #ifdef MP
			// 小程序静默授权
			if (!store.getters.isLogin && !this.globalData.tokenIsExist) {
				Routine.getCode().then(code => {
						let userInfo = {
							code: code,
							spreadPid: this.globalData.spread, //获取推广人ID
							type: 'routine'
						};
						Routine.authUserInfo(userInfo)
					})
					.catch(res => {
						uni.hideLoading();
					});
			}
			// #endif
			if (store.getters.isLogin && this.globalData.spread) {
				spread(this.globalData.spread).then(res => {}).catch(res => {})
			}

            //配置项存储
			store.commit('GLOBAL_DATA', JSON.parse(JSON.stringify(that.globalData)));
		},
		async mounted() {
			//if (this.$store.getters.isLogin && !this.$Cache.get('USER_INFO')) await this.$store.dispatch('USERINFO');
		},
		methods: {
			//获取登录配置
			getLoginConfig() {
				loginConfigApi().then(res => {
					console.log(res)
					let data = res.data;
					//公众号登录方式(单选),1微信授权，2手机号登录
					this.globalData.publicLoginType = data.wechatBrowserVisit; 
					this.$Cache.set('publicLoginType', data.wechatBrowserVisit);
					//小程序手机号校验类型（多选）1微信小程序验证 2短信验证
					this.globalData.routinePhoneVerification = data.routinePhoneVerification; 
					//公司名称
					this.globalData.companyName = data.siteName || '欢迎你';
					uni.setStorageSync('companyName', data.siteName);
					//登录页logo
					this.globalData.mobileLoginLogo = data.mobileLoginLogo;
					uni.setStorageSync('mobileLoginLogo', data.mobileLoginLogo);
				})
			}
		},
		onShow: function() {
			// #ifdef H5
			uni.getSystemInfo({
				success(e) {
					/* 窗口宽度大于420px且不在PC页面且不在移动设备时跳转至 PC.html 页面 */
					if (e.windowWidth > 420 && !window.top.isPC && !/iOS|Android/i.test(e.system)) {
						// window.location.pathname = 'https://java.crmeb.net/';
						/* 若你的项目未设置根目录（默认为 / 时），则使用下方代码 */
						window.location.pathname = '/static/html/pc.html';
					}
				}
			})
			// #endif
		},
		onHide: function() {}
	}
</script>
<style lang="scss">
	/* #ifndef APP-PLUS-NVUE || APP-NVUE */
	@import "@/plugin/animate/animate.min.css";
	@import 'static/css/base.css';
	@import 'static/iconfont/iconfont.css';
	@import 'static/css/guildford.css';
	@import 'static/css/style.scss';


	view {
		box-sizing: border-box;
	}

	body::-webkit-scrollbar,
	html::-webkit-scrollbar {
		display: none;
	}

	.bg-color-red {
		background-color: #E93323;
	}

	.syspadding {
		padding-top: var(--status-bar-height);
	}

	.flex {
		display: flex;
	}

	/deep/.uni-scroll-view::-webkit-scrollbar {
		/* 隐藏滚动条，但依旧具备可以滚动的功能 */
		display: none
	}

	::-moz-scrollbar {
		width: 0;
		height: 0;
		color: transparent;
	}

	::-webkit-scrollbar {
		display: none;
		width: 0 !important;
		height: 0 !important;
		-webkit-appearance: none;
		background: transparent;
	}

	/*#endif*/
</style>