<template>
  <div class="app">
    <transition name="fade" mode="out-in">
      <section v-if="view==='auth'" key="auth" class="auth-google">
        <div class="auth-shell">
          <div class="auth-brand">
            <div class="auth-logo">C</div>
            <div>
              <h1>龙岩学院</h1>
              <p>校园地图与建筑导览系统</p>
            </div>
          </div>

          <div class="auth-switch">
            <button :class="{on:tab==='login'}" @click="tab='login'">登录</button>
            <button :class="{on:tab==='register'}" @click="tab='register'">注册</button>
          </div>

          <form v-if="tab==='login'" class="auth-form" @submit.prevent="doLogin">
            <label class="auth-field">
              <span>用户名</span>
              <div class="auth-input">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"></circle><path d="M20 20l-3.5-3.5"></path></svg>
                <input v-model.trim="loginForm.username" placeholder="请输入用户名" required />
              </div>
            </label>

            <label class="auth-field">
              <span>密码</span>
              <div class="auth-input">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="11" width="16" height="9" rx="2"></rect><path d="M8 11V8a4 4 0 0 1 8 0v3"></path></svg>
                <input v-model.trim="loginForm.password" type="password" placeholder="请输入密码" required />
              </div>
            </label>

            <div class="auth-role">
              <button type="button" :class="{on:loginForm.role==='user'}" @click="loginForm.role='user'">普通用户</button>
              <button type="button" :class="{on:loginForm.role==='admin'}" @click="loginForm.role='admin'">管理员</button>
            </div>

            <button class="auth-submit" :disabled="loading.auth">{{loading.auth?'登录中...':'登录'}}</button>
          </form>

          <form v-else class="auth-form" @submit.prevent="doRegister">
            <label class="auth-field">
              <span>用户名</span>
              <div class="auth-input">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="7"></circle><path d="M20 20l-3.5-3.5"></path></svg>
                <input v-model.trim="registerForm.username" placeholder="请输入用户名" required />
              </div>
            </label>

            <label class="auth-field">
              <span>密码</span>
              <div class="auth-input">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="4" y="11" width="16" height="9" rx="2"></rect><path d="M8 11V8a4 4 0 0 1 8 0v3"></path></svg>
                <input v-model.trim="registerForm.password" type="password" placeholder="请输入密码" required />
              </div>
            </label>

            <label class="auth-field">
              <span>确认密码</span>
              <div class="auth-input">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"></path></svg>
                <input v-model.trim="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" required />
              </div>
            </label>

            <button class="auth-submit" :disabled="loading.auth">{{loading.auth?'注册中...':'注册'}}</button>
          </form>
        </div>
      </section>

      <section v-else-if="view==='user'" key="user" class="user relative">
        <!-- 导览开屏加载动画 -->
        <transition name="fade-slow">
          <div v-if="showSplash" class="absolute inset-0 z-[100] flex flex-col items-center justify-center bg-[#e9e5dc]/90 backdrop-blur-md">
            <div class="text-center animate-rise-up">
              <div class="w-20 h-20 mx-auto bg-slate-800 rounded-2xl shadow-2xl shadow-slate-800/30 flex items-center justify-center mb-8 transform transition-transform hover:scale-105">
                <svg class="w-10 h-10 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
              </div>
              <h1 class="text-4xl font-extrabold text-slate-800 tracking-widest mb-4 drop-shadow-sm">欢迎来到龙岩学院</h1>
              <p class="text-slate-500 tracking-widest font-medium text-lg">龙岩学院校园导览系统</p>
              <div class="mt-10 h-1 w-56 mx-auto bg-slate-200/60 rounded-full overflow-hidden">
                <div class="h-full bg-slate-800 rounded-full animate-progress origin-left"></div>
              </div>
            </div>
          </div>
        </transition>

        <header class="google-topbar">
          <div class="brand">
            <div class="logo-box">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2L2 7l10 5 10-5-10-5z"/><path d="M2 17l10 5 10-5"/><path d="M2 12l10 5 10-5"/></svg>
            </div>
            <h2>龙岩学院校园导览系统</h2>
          </div>
          <div class="search-box">
            <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="8"/><path d="M21 21l-4.35-4.35"/></svg>
            <input v-model.trim="keyword" placeholder="在校园中搜索建筑、地标..." />
            <div class="search-stats" v-if="keyword || showFavoritesOnly">{{filtered.length}} 结果</div>
            <button class="ml-2 p-1.5 rounded-lg transition" :class="showFavoritesOnly ? 'bg-amber-100 text-amber-600' : 'text-slate-400 hover:bg-slate-100'" @click="showFavoritesOnly = !showFavoritesOnly" title="我的收藏">
              <svg class="w-5 h-5" :fill="showFavoritesOnly ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                <path stroke-linecap="round" stroke-linejoin="round" d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" />
              </svg>
            </button>
          </div>
          <div class="actions">
            <button class="btn-icon" v-if="user?.role==='admin'" @click="goAdmin" title="管理后台">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
              <span>管理</span>
            </button>
            <button class="btn-icon danger" @click="doLogout" title="退出">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
              <span>退出</span>
            </button>
          </div>
        </header>
        <main class="main">
          <section class="map-wrap">
            <div class="map">
              <img :src="mapUrl" alt="main map" />
              <button v-for="(b,index) in filtered" :key="b.id" class="marker hover-name" :class="{spread:markerSpread}" :style="markerStyle(b,index)" @click="openDetail(b.id)">
                <i></i><span>{{b.name}}</span>
              </button>
            </div>
          </section>
          <aside class="panel p-0 flex flex-col relative bg-white border-l border-slate-200 z-10 shadow-[-4px_0_15px_rgba(0,0,0,0.05)] overflow-hidden">
            <transition name="slide" mode="out-in">
              <div v-if="active" :key="active.id" class="flex flex-col h-full">
                <!-- 顶部照片 -->
                <div class="relative w-full h-64 bg-slate-100 flex-shrink-0">
                  <img v-if="viewerImages.length" :src="imgUrl(viewerImages[0])" class="w-full h-full object-cover cursor-pointer" @click="openGallery(0)" />
                  <div v-else class="w-full h-full flex items-center justify-center text-slate-400">暂无图片</div>
                  
                  <!-- 关闭按钮 -->
                  <button class="absolute top-4 right-4 h-9 w-9 rounded-full bg-white/90 grid place-items-center text-slate-600 hover:bg-white hover:text-slate-900 shadow transition border-0" @click="active=null">
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                  </button>
                </div>

                <!-- 滚动内容区 -->
                <div class="flex-1 overflow-auto pb-6">
                  <div class="px-5 pt-5 pb-2 flex justify-between items-start">
                    <div>
                      <h3 class="text-2xl font-bold text-slate-900 m-0 mb-1">{{active.name}}</h3>
                      <p class="text-[13px] font-medium inline-block px-2.5 py-1 rounded-md m-0" :class="catColor(active.category)">{{catEmoji(active.category)}} {{active.category||'未分类'}}</p>
                    </div>
                    <button class="h-10 w-10 rounded-full flex items-center justify-center transition-colors" :class="isFavorited ? 'text-amber-500 bg-amber-50' : 'text-slate-400 bg-slate-50'" @click="doToggleFavorite">
                      <svg class="w-6 h-6" :fill="isFavorited ? 'currentColor' : 'none'" viewBox="0 0 24 24" stroke="currentColor" stroke-width="2">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M11.48 3.499a.562.562 0 011.04 0l2.125 5.111a.563.563 0 00.475.345l5.518.442c.499.04.701.663.321.988l-4.204 3.602a.563.563 0 00-.182.557l1.285 5.385a.562.562 0 01-.84.61l-4.725-2.885a.563.563 0 00-.586 0L6.982 20.54a.562.562 0 01-.84-.61l1.285-5.386a.562.562 0 00-.182-.557l-4.204-3.602a.563.563 0 01.321-.988l5.518-.442a.563.563 0 00.475-.345L11.48 3.5z" />
                      </svg>
                    </button>
                  </div>

                  <div class="px-5">
                    <div class="detail-tabs-underline mb-5">
                      <button :class="{on:detailTab==='overview'}" @click="detailTab='overview'">概览</button>
                      <button :class="{on:detailTab==='detail'}" @click="detailTab='detail'">详细信息</button>
                      <button :class="{on:detailTab==='comments'}" @click="detailTab='comments'">评论 ({{comments.length}})</button>
                    </div>
                    
                    <div v-if="detailTab==='overview'" class="space-y-6">
                      <div class="detail-section">
                        <h4 class="detail-section-title">概况</h4>
                        <p class="desc m-0">{{active.description||'暂无简介'}}</p>
                      </div>
                      
                      <div class="detail-section" v-if="viewerImages.length > 1">
                        <h4 class="detail-section-title">照片</h4>
                        <div class="photo-grid">
                          <div v-for="(img, i) in viewerImages.slice(1, 5)" :key="i" class="photo-item" @click="openGallery(i+1)">
                            <img :src="imgUrl(img)" alt="img" />
                            <div v-if="i===3 && viewerImages.length>5" class="more-photos">+{{viewerImages.length-5}}</div>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div v-if="detailTab==='detail'">
                      <div class="detail-section">
                        <h4 class="detail-section-title">详细信息</h4>
                        <div class="detail-text" v-html="active.detail?.replace(/\n/g, '<br>') || '暂无详细介绍'"></div>
                      </div>
                    </div>

                    <div v-if="detailTab==='comments'" class="space-y-4">
                      <div class="flex gap-2 mb-6">
                        <input v-model="commentInput" class="flex-1 h-10 px-4 rounded-lg border border-slate-200 text-sm focus:outline-none focus:border-slate-400" placeholder="写下你的评论..." @keyup.enter="doAddComment" />
                        <button class="h-10 px-4 bg-slate-800 text-white text-sm font-medium rounded-lg hover:bg-slate-700 transition" @click="doAddComment">发表</button>
                      </div>

                      <div v-for="c in comments" :key="c.id" class="p-4 rounded-xl bg-slate-50 relative group">
                        <div class="flex items-center gap-3 mb-2">
                          <div class="w-8 h-8 rounded-full bg-slate-200 flex items-center justify-center text-xs font-bold text-slate-500 overflow-hidden">
                            <img v-if="c.avatar" :src="imgUrl(c.avatar)" class="w-full h-full object-cover" />
                            <span v-else>{{c.username?.slice(0,1).toUpperCase()}}</span>
                          </div>
                          <div>
                            <div class="text-sm font-bold text-slate-900">{{c.username}}</div>
                            <div class="text-[11px] text-slate-400">{{new Date(c.createTime).toLocaleString()}}</div>
                          </div>
                        </div>
                        <div class="text-sm text-slate-700 leading-relaxed">{{c.content}}</div>
                        <button v-if="user?.role==='admin' || user?.id===c.userId" class="absolute top-4 right-4 text-slate-300 hover:text-red-500 opacity-0 group-hover:opacity-100 transition" @click="doDeleteComment(c.id)">
                          <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
                        </button>
                      </div>
                      <div v-if="!comments.length" class="text-center py-10 text-slate-400 text-sm">暂无评论，快来抢沙发吧</div>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else key="empty" class="flex-1 grid place-items-center text-slate-400">点击地图点位查看详情</div>
            </transition>
          </aside>
        </main>
      </section>

      <section v-else key="admin" class="admin">
        <header class="google-topbar">
          <div class="brand">
            <div class="logo-box bg-slate-800">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 20h9"/><path d="M16.5 3.5a2.121 2.121 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>
            </div>
            <h2>管理后台</h2>
          </div>
          <div class="flex-1 flex justify-center">
            <div class="flex bg-slate-100 p-1 rounded-xl">
              <button class="px-6 py-2 rounded-lg text-sm font-medium transition" :class="adminTab==='building' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-700'" @click="adminTab='building'">建筑管理</button>
              <button class="px-6 py-2 rounded-lg text-sm font-medium transition" :class="adminTab==='user' ? 'bg-white text-slate-900 shadow-sm' : 'text-slate-500 hover:text-slate-700'" @click="adminTab='user'">用户管理</button>
            </div>
          </div>
          <div class="actions">
            <button class="btn-icon" @click="view='user'" title="返回导览">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"/><polyline points="9 22 9 12 15 12 15 22"/></svg>
              <span>导览</span>
            </button>
            <button class="btn-icon danger" @click="doLogout" title="退出">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" y1="12" x2="9" y2="12"/></svg>
              <span>退出</span>
            </button>
          </div>
        </header>
        <main class="admin-main">
          <template v-if="adminTab==='building'">
            <aside class="admin-sidebar">
              <div class="admin-sidebar-hd">
                <h3 class="text-lg font-bold text-slate-800 m-0">建筑列表</h3>
                <span class="text-xs bg-slate-100 text-slate-500 px-2 py-1 rounded-md">{{adminFiltered.length}} 项</span>
              </div>
              <div class="admin-sidebar-list">
                <button v-for="b in adminFiltered" :key="b.id" class="item" :class="{on:form.id===b.id}" @click="pick(b.id)">
                  <div><strong>{{b.name}}</strong><small class="inline-block px-1.5 py-0.5 rounded text-[11px] mt-1" :class="catColor(b.category)">{{catEmoji(b.category)}} {{b.category||'未分类'}}</small></div>
                  <span class="text-xs font-mono text-slate-400">#{{b.id}}</span>
                </button>
              </div>
            </aside>

            <section class="admin-content">
              <div class="edit-card" v-if="form.id">
                <div class="hero-header">
                  <img v-if="imageDrafts.length && !imageDrafts[0].isNew" :src="imgUrl(imageDrafts[0].url)" />
                  <img v-else-if="imageDrafts.length && imageDrafts[0].url" :src="imageDrafts[0].url" />
                  <div class="hero-overlay"></div>
                  <div class="hero-info">
                    <div class="hero-avatar">{{(form.name||'B').slice(0,2)}}</div>
                    <div class="hero-text">
                      <h3>{{form.name}}</h3>
                      <p>编辑简介、详细信息与图片</p>
                    </div>
                  </div>
                </div>

                <form class="edit-form-wrap" @submit.prevent="saveAll">
                  <div class="g-field">
                    <label>简介</label>
                    <textarea v-model.trim="form.description" rows="3" placeholder="请输入简短的概览介绍，如“欢迎来到青年广场”"></textarea>
                  </div>
                  
                  <div class="g-field">
                    <label>详细信息</label>
                    <textarea v-model.trim="form.detail" rows="7" placeholder="请输入完整的历史、功能等详细介绍"></textarea>
                  </div>

                  <div class="image-manage mt-6">
                    <div class="image-manage-hd">
                      <h4>图片管理</h4>
                      <button type="button" class="h-8 rounded-full border border-slate-200 bg-slate-100 px-4 text-sm font-medium transition hover:bg-slate-200" @click="reloadDraft">重置修改</button>
                    </div>
                    <div class="upload-input-wrap mb-4">
                      <input type="file" multiple accept="image/*" @change="onSelectFiles" />
                    </div>
                    <div class="imgs">
                      <div v-for="(im,i) in imageDrafts" :key="im.localKey" class="im-row">
                        <img :src="imgUrl(im.url)" @click="openPreviewByUrl(im.url)" />
                        <div class="img-meta flex-1 overflow-hidden px-2">
                          <span class="block truncate text-sm text-slate-600">{{im.remoteUrl || im.url}}</span>
                          <small v-if="im.isNew && !im.remoteUrl" class="text-amber-600">仅本地预览，未上传</small>
                          <small v-else-if="im.isNew && im.remoteUrl" class="text-green-600">已上传，待保存入库</small>
                        </div>
                        <button class="h-8 px-3 rounded-lg border border-red-200 text-red-600 hover:bg-red-50 transition" type="button" @click="removeDraft(i)">移除</button>
                      </div>
                      <div v-if="!imageDrafts.length" class="empty">暂无图片</div>
                    </div>
                  </div>

                  <div class="save-bar mt-8 pt-6 border-t border-slate-100 flex items-center justify-end gap-4">
                    <span v-if="dirty" class="text-sm font-medium text-amber-600 bg-amber-50 px-3 py-1 rounded-full">有未保存变更</span>
                    <button class="save-btn px-8 h-12 text-lg shadow-blue-500/30" :disabled="loading.save">{{loading.save?'保存中...':'保存全部变更'}}</button>
                  </div>
                </form>
              </div>

              <div class="edit-card grid place-items-center h-64 text-slate-400" v-else>
                <div class="text-center">
                  <svg class="w-16 h-16 mx-auto mb-4 text-slate-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
                  </svg>
                  <p class="text-lg">请在左侧选择要编辑的建筑</p>
                </div>
              </div>
            </section>
          </template>

          <template v-else>
            <div class="flex-1 p-8 overflow-auto">
              <div class="max-w-5xl mx-auto">
                <div class="flex items-center justify-between mb-8">
                  <div>
                    <h3 class="text-2xl font-bold text-slate-900">用户管理</h3>
                    <p class="text-slate-500 mt-1">管理系统用户信息与权限</p>
                  </div>
                  <div class="px-4 py-2 bg-slate-100 rounded-lg text-sm font-medium text-slate-600">
                    总计 {{users.length}} 位用户
                  </div>
                </div>

                <div class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden">
                  <table class="w-full text-left border-collapse">
                    <thead>
                      <tr class="bg-slate-50">
                        <th class="px-6 py-4 text-sm font-bold text-slate-600 border-b border-slate-100">用户</th>
                        <th class="px-6 py-4 text-sm font-bold text-slate-600 border-b border-slate-100">权限角色</th>
                        <th class="px-6 py-4 text-sm font-bold text-slate-600 border-b border-slate-100">注册时间</th>
                        <th class="px-6 py-4 text-sm font-bold text-slate-600 border-b border-slate-100 text-right">操作</th>
                      </tr>
                    </thead>
                    <tbody class="divide-y divide-slate-50">
                      <tr v-for="u in users" :key="u.id" class="hover:bg-slate-50/50 transition">
                        <td class="px-6 py-4">
                          <div class="flex items-center gap-3">
                            <div class="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-slate-500 font-bold overflow-hidden">
                              <img v-if="u.avatar" :src="imgUrl(u.avatar)" class="w-full h-full object-cover" />
                              <span v-else>{{u.username.slice(0,1).toUpperCase()}}</span>
                            </div>
                            <div>
                              <div class="font-bold text-slate-900">{{u.username}}</div>
                              <div class="text-xs text-slate-400">ID: {{u.id}}</div>
                            </div>
                          </div>
                        </td>
                        <td class="px-6 py-4">
                          <span class="px-3 py-1 rounded-full text-xs font-bold" :class="u.role==='admin' ? 'bg-indigo-50 text-indigo-600' : 'bg-slate-100 text-slate-600'">
                            {{u.role==='admin' ? '管理员' : '普通用户'}}
                          </span>
                        </td>
                        <td class="px-6 py-4 text-sm text-slate-500">
                          {{new Date(u.createTime).toLocaleDateString()}}
                        </td>
                        <td class="px-6 py-4 text-right">
                          <div class="flex items-center justify-end gap-2">
                            <button class="h-8 px-3 rounded-lg text-xs font-bold transition" :class="u.role==='admin' ? 'text-slate-600 bg-slate-100 hover:bg-slate-200' : 'text-indigo-600 bg-indigo-50 hover:bg-indigo-100'" @click="doUpdateUserRole(u)">
                              {{u.role==='admin' ? '设为用户' : '设为管理员'}}
                            </button>
                            <button class="h-8 px-3 rounded-lg text-xs font-bold text-red-600 bg-red-50 hover:bg-red-100 transition" @click="doDeleteUser(u.id)">
                              删除
                            </button>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </template>
        </main>
      </section>
    </transition>

    <transition name="toast"><div v-if="toast.show" class="toast" :class="toast.type">{{toast.text}}</div></transition>
    <transition name="fade"><div v-if="showPreview" class="preview-mask" @click="showPreview=false"><img class="preview-image" :src="previewUrl" @click.stop /></div></transition>

    <transition name="fade">
      <div v-if="showGallery" class="gallery-mask" @click="showGallery=false">
        <button class="gallery-close" @click="showGallery=false">✕</button>
        <div class="gallery-layout" @click.stop>
          <div class="gallery-sidebar">
            <div class="gallery-sidebar-hd">照片和视频</div>
            <div class="gallery-thumbs">
              <div v-for="(img, i) in viewerImages" :key="i" class="thumb-item" :class="{active: idx===i}" @click="idx=i">
                <img :src="imgUrl(img)" />
              </div>
            </div>
          </div>
          <div class="gallery-main">
            <img :src="imgUrl(viewerImages[idx])" class="gallery-main-img" />
            <button class="nav l" @click="prev" v-if="viewerImages.length>1">‹</button>
            <button class="nav r" @click="next" v-if="viewerImages.length>1">›</button>
            <div class="idx">{{idx+1}} / {{viewerImages.length}}</div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, watch } from 'vue'
import { 
  currentUser, getBuildingDetail, getBuildingImages, getBuildingList, login, logout, register, 
  replaceBuildingImages, updateBuilding, uploadFile, uploadFilesBatch,
  getComments, addComment, deleteComment,
  toggleFavorite, getFavoriteStatus, getMyFavoriteIds,
  getUserList, adminUpdateUser, adminDeleteUser, updateProfile
} from './api/http'

const mapUrl='/api/images/main.jpg', MW=1024, MH=722
const view=ref('auth'), tab=ref('login'), user=ref(null), markerSpread=ref(false)
const loginForm=reactive({username:'',password:'',role:'user'}), registerForm=reactive({username:'',password:'',confirmPassword:''})
const loading=reactive({auth:false,save:false})
const buildings=ref([]), keyword=ref(''), ak=ref(''), showFavoritesOnly=ref(false), favoriteIds=ref([])
const adminTab=ref('building'), users=ref([])
const active=ref(null), viewerImages=ref([]), idx=ref(0)
const comments=ref([]), isFavorited=ref(false), commentInput=ref('')
const form=reactive({id:null,name:'',category:'',description:'',detail:'',coverImage:'',mapX:0,mapY:0})
const imageDrafts=ref([]), dirty=ref(false)
const showPreview=ref(false), previewUrl=ref('')
const showGallery=ref(false)
const detailTab=ref('overview')
const toast=reactive({show:false,text:'',type:'ok',timer:null})
const showSplash=ref(false)

watch(view, (newVal) => {
  if (newVal === 'user') {
    showSplash.value = true
    setTimeout(() => { showSplash.value = false }, 2000)
  }
})

const tip=(t,tp='ok')=>{toast.text=t;toast.type=tp;toast.show=true;if(toast.timer)clearTimeout(toast.timer);toast.timer=setTimeout(()=>toast.show=false,2200)}

const catEmoji = (category) => {
  const map = {
    '教学楼': '📚',
    '宿舍楼': '🛏️',
    '体育馆': '🏀',
    '图书馆': '📖',
    '食堂': '🍽️',
    '行政楼': '🏢',
    '实验楼': '🔬',
    '风景': '🌳',
    '地标': '📍'
  }
  return map[category] || '📍'
}

const catColor = (category) => {
  const map = {
    '教学楼': 'text-blue-700 bg-blue-50',
    '宿舍楼': 'text-emerald-700 bg-emerald-50',
    '体育馆': 'text-orange-700 bg-orange-50',
    '图书馆': 'text-purple-700 bg-purple-50',
    '食堂': 'text-rose-700 bg-rose-50',
    '行政楼': 'text-slate-700 bg-slate-100',
    '实验楼': 'text-cyan-700 bg-cyan-50',
    '风景': 'text-green-700 bg-green-50',
    '地标': 'text-indigo-700 bg-indigo-50'
  }
  return map[category] || 'text-slate-700 bg-slate-50'
}

const imgUrl=u=>{
  if(!u) return ''
  if(u.startsWith('http') || u.startsWith('blob:') || u.startsWith('data:')) return u
  // 处理本地图片路径 - 如果是/images/开头的路径，加上 /src/assets 前缀
  if(u.startsWith('/images/')) return `/src/assets${u}`
  // 兼容直接存完整路径的情况
  if(u.startsWith('/src/assets/images/')) return u
  return `/api${u.startsWith('/')?'':'/'}${u}`
}
const markerStyle=(b,index)=>({left:`${(Number(b.mapX||0)/MW)*100}%`,top:`${(Number(b.mapY||0)/MH)*100}%`,'--delay':`${index*20}ms`})
const filtered=computed(()=>{
  let res = buildings.value
  if(keyword.value) res = res.filter(b=>(b.name||'').includes(keyword.value))
  if(showFavoritesOnly.value) res = res.filter(b=>favoriteIds.value.includes(b.id))
  return res
})
const adminFiltered=computed(()=>!ak.value?buildings.value:buildings.value.filter(b=>(b.name||'').includes(ak.value)))
const setSpread=()=>{markerSpread.value=false;setTimeout(()=>markerSpread.value=true,60)}
watch(filtered,()=>setSpread())

const load=async()=>{
  const [r, f]=await Promise.all([getBuildingList(), getMyFavoriteIds()])
  buildings.value=Array.isArray(r.data?.data)?r.data.data:[]
  favoriteIds.value=f.data?.data||[]
}
const assign=d=>Object.assign(form,{id:d.id??null,name:d.name??'',category:d.category??'',description:d.description??'',detail:d.detail??'',coverImage:d.coverImage??'',mapX:d.mapX??0,mapY:d.mapY??0})

const doLogin=async()=>{loading.auth=true;try{const r=await login({username:loginForm.username,password:loginForm.password});if(r.data?.code!==200||!r.data?.data)return tip(r.data?.message||'登录失败','err');if(r.data.data.role!==loginForm.role)return tip('身份与账号不一致','err');user.value=r.data.data;await load();view.value=user.value.role==='admin'?'admin':'user';if(view.value==='admin'&&buildings.value.length)await pick(buildings.value[0].id);setSpread();tip('登录成功')}catch{tip('登录异常','err')}finally{loading.auth=false}}
const doRegister=async()=>{if(registerForm.password!==registerForm.confirmPassword)return tip('两次密码不一致','err');loading.auth=true;try{const r=await register({username:registerForm.username,password:registerForm.password});if(r.data?.code!==200)return tip(r.data?.message||'注册失败','err');tab.value='login';loginForm.username=registerForm.username;loginForm.password=registerForm.password;registerForm.username='';registerForm.password='';registerForm.confirmPassword='';tip('注册成功，请登录')}catch{tip('注册异常','err')}finally{loading.auth=false}}
const doLogout=async()=>{try{await logout()}catch{}view.value='auth';user.value=null;active.value=null;tip('已退出')}
const goAdmin=async()=>{
  view.value='admin';
  adminTab.value='building';
  if(form.id==null&&buildings.value.length)await pick(buildings.value[0].id)
}

const loadUsers=async()=>{
  const r=await getUserList()
  users.value=r.data?.data||[]
}

const doUpdateUserRole=async(u)=>{
  const newRole = u.role === 'admin' ? 'user' : 'admin'
  const r=await adminUpdateUser({id:u.id, role:newRole})
  if(r.data?.code===200){
    u.role = newRole
    tip('更新成功')
  }
}

const doDeleteUser=async(id)=>{
  if(!confirm('确定删除该用户吗？')) return
  const r=await adminDeleteUser(id)
  if(r.data?.code===200){
    users.value=users.value.filter(u=>u.id!==id)
    tip('删除成功')
  }
}

watch(adminTab, (val) => {
  if(val === 'user') loadUsers()
})

const openDetail=async(id)=>{
  const [d,im,c,f]=await Promise.all([
    getBuildingDetail(id),
    getBuildingImages(id),
    getComments(id),
    getFavoriteStatus(id)
  ]);
  active.value=d.data?.data||null;
  viewerImages.value=(im.data?.data||[]).map(x=>x.imageUrl);
  comments.value=c.data?.data||[];
  isFavorited.value=f.data?.data||false;
  idx.value=0;
  detailTab.value='overview'
}

const doToggleFavorite=async()=>{
  if(!active.value) return
  const r=await toggleFavorite(active.value.id)
  if(r.data?.code===200){
    isFavorited.value=r.data.data
    if(isFavorited.value) favoriteIds.value.push(active.value.id)
    else favoriteIds.value = favoriteIds.value.filter(id => id !== active.value.id)
    tip(isFavorited.value?'已收藏':'已取消收藏')
  }
}

const doAddComment=async()=>{
  if(!commentInput.value.trim()) return
  const r=await addComment({buildingId:active.value.id,content:commentInput.value})
  if(r.data?.code===200){
    commentInput.value=''
    const res=await getComments(active.value.id)
    comments.value=res.data?.data||[]
    tip('发表成功')
  }
}

const doDeleteComment=async(id)=>{
  const r=await deleteComment(id)
  if(r.data?.code===200){
    comments.value=comments.value.filter(c=>c.id!==id)
    tip('删除成功')
  }
}
const prev=()=>viewerImages.value.length&&(idx.value=(idx.value-1+viewerImages.value.length)%viewerImages.value.length)
const next=()=>viewerImages.value.length&&(idx.value=(idx.value+1)%viewerImages.value.length)
const openPreviewByUrl=url=>{previewUrl.value=imgUrl(url);showPreview.value=true}
const openGallery=(i)=>{idx.value=i;showGallery.value=true}

const buildDraftFromDb=(arr)=>arr.map(it=>({localKey:`db-${it.id}`,id:it.id,url:it.imageUrl,isNew:false,isLocalPreview:false}))
const releaseLocalDraftUrls=()=>{
  imageDrafts.value.forEach((d)=>{
    if(d?.isLocalPreview && d?.url?.startsWith('blob:')) URL.revokeObjectURL(d.url)
  })
}
const pick=async(id)=>{const [d,im]=await Promise.all([getBuildingDetail(id),getBuildingImages(id)]);if(!d.data?.data)return;assign(d.data.data);releaseLocalDraftUrls();imageDrafts.value=buildDraftFromDb(im.data?.data||[]);dirty.value=false}
const reloadDraft=()=>{if(form.id) pick(form.id)}
const removeDraft=(i)=>{
  const target=imageDrafts.value[i]
  if(target?.isLocalPreview && target?.url?.startsWith('blob:')){
    URL.revokeObjectURL(target.url)
  }
  imageDrafts.value.splice(i,1)
  dirty.value=true
}

const onSelectFiles=async(e)=>{
  const files=e.target.files
  if(!files||!files.length||!form.id){e.target.value='';return}

  const selected = Array.from(files)
  const tempDrafts = selected.map((file, i) => ({
    localKey: `local-${Date.now()}-${i}`,
    id: null,
    url: URL.createObjectURL(file),
    isNew: true,
    isLocalPreview: true,
    remoteUrl: null
  }))

  imageDrafts.value.push(...tempDrafts)
  dirty.value=true

  try{
    // 使用批量上传API
    const r=await uploadFilesBatch(files)
    if(r.data?.code===200&&Array.isArray(r.data?.data)){
      const urls=r.data.data
      
      // 更新临时草稿的远程URL
      tempDrafts.forEach((d, idx2) => {
        if (urls[idx2]) {
          d.remoteUrl = urls[idx2]
        }
      })
      
      tip(`已选择 ${selected.length} 张，上传成功 ${urls.length} 张，点击保存后入库`)
    } else {
      throw new Error('上传失败')
    }
  }catch{
    tip('文件已本地预览，上传失败，请稍后重试', 'err')
  }
  e.target.value=''
}

const saveAll=async()=>{
  if(!form.id)return tip('请先选择建筑','err')
  loading.save=true
  try{
    const detailRes=await updateBuilding({id:form.id,name:form.name,category:form.category,mapX:Number(form.mapX),mapY:Number(form.mapY),description:form.description,detail:form.detail,coverImage:form.coverImage})
    if(detailRes.data?.code!==200) return tip(detailRes.data?.message||'详情保存失败','err')

    const keepIds=imageDrafts.value.filter(x=>!x.isNew&&x.id).map(x=>x.id)
    const newUrls=imageDrafts.value
      .filter(x=>x.isNew)
      .map(x=>x.remoteUrl || (!x.isLocalPreview ? x.url : null))
      .filter(Boolean)

    const pendingLocalCount = imageDrafts.value.filter(x=>x.isNew && !x.remoteUrl).length
    if(pendingLocalCount>0){
      tip(`有 ${pendingLocalCount} 张图片仅本地预览未上传，保存时将跳过`, 'err')
    }
    const imgRes=await replaceBuildingImages({buildingId:form.id,keepImageIds:keepIds,newImageUrls:newUrls})
    if(imgRes.data?.code!==200) return tip(imgRes.data?.message||'图片保存失败','err')

    await pick(form.id)
    await load()
    dirty.value=false
    tip('保存成功（详情+图片）')
  }catch{tip('保存失败','err')}finally{loading.save=false}
}

onBeforeUnmount(()=>{
  releaseLocalDraftUrls()
})

onMounted(async()=>{
  try{
    const r=await currentUser()
    if(r.data?.code===200&&r.data?.data){
      user.value=r.data.data
      await load()
      view.value=user.value.role==='admin'?'admin':'user'
      if(view.value==='admin'&&buildings.value.length) await pick(buildings.value[0].id)
      if(view.value==='user') { showSplash.value=true; setTimeout(()=>{showSplash.value=false}, 2000) }
      setSpread()
      return
    }
  }catch{}
  try{await load();setSpread()}catch{tip('建筑数据加载失败','err')}
})
</script>
