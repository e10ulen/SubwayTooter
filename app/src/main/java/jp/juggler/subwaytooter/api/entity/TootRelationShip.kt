package jp.juggler.subwaytooter.api.entity

import jp.juggler.subwaytooter.api.TootParser
import org.json.JSONObject

import jp.juggler.subwaytooter.table.UserRelation
import jp.juggler.subwaytooter.util.parseLong

class TootRelationShip(parser:TootParser,src : JSONObject) {
	
	// Target account id
	// MisskeyのユーザリレーションはuserIdを含まないので後から何か設定する必要がある
	var id : EntityId
	
	// Whether the authorized user is currently following the target account.
	// maybe faked in response of follow-request.
	val following : Boolean
	
	// Whether the authorized user is currently being followed by the target account.
	val followed_by : Boolean
	
	// Whether the authorized user is currently blocking the target account.
	val blocking : Boolean

	// misskey用
	val blocked_by : Boolean
	
	// Whether the authorized user is currently muting the target account.
	val muting : Boolean
	
	// Whether the authorized user has requested to follow the target account.
	// maybe true while follow-request is progress on server, even if the user is not locked.
	val requested : Boolean
	
	// (mastodon 2.1 or later) per-following-user setting.
	// Whether the boosts from target account will be shown on authorized user's home TL.
	val showing_reblogs : Int
	
	// 「プロフィールで紹介する」「プロフィールから外す」
	val endorsed : Boolean
	
	// misskey用
	val requested_by : Boolean

	init {
		
		if( parser.serviceType == ServiceType.MISSKEY){
			this.id = EntityId.defaultString
			
			following = src.optBoolean("isFollowing")
			followed_by = src.optBoolean("isFollowed")
			muting = src.optBoolean("isMuted")
			blocking = src.optBoolean("isBlocking")
			blocked_by = src.optBoolean("isBlocked")
			requested = src.optBoolean("hasPendingFollowRequestFromYou")
			requested_by = src.optBoolean("hasPendingFollowRequestToYou")
			
			endorsed = false
			showing_reblogs = UserRelation.REBLOG_UNKNOWN
			
		}else{
			this.id = EntityId.mayDefault( src.parseLong("id") )
			
			var ov = src.opt("following")
			if(ov is JSONObject) {
				// https://github.com/tootsuite/mastodon/issues/5856
				// 一部の開発版ではこうなっていた
				
				this.following = true
				
				ov = ov.opt("reblogs")
				if(ov is Boolean) {
					this.showing_reblogs = if(ov) UserRelation.REBLOG_SHOW else UserRelation.REBLOG_HIDE
				} else {
					this.showing_reblogs = UserRelation.REBLOG_UNKNOWN
				}
			} else {
				// 2.0 までの挙動
				this.following = if(ov is Boolean) ov else false
				
				// 2.1 の挙動
				ov = src.opt("showing_reblogs")
				if(this.following && ov is Boolean) {
					this.showing_reblogs = if(ov) UserRelation.REBLOG_SHOW else UserRelation.REBLOG_HIDE
				} else {
					this.showing_reblogs = UserRelation.REBLOG_UNKNOWN
				}
				
			}
			
			this.followed_by = src.optBoolean("followed_by")
			this.blocking = src.optBoolean("blocking")
			this.muting = src.optBoolean("muting")
			this.requested = src.optBoolean("requested")
			this.endorsed = src.optBoolean("endorsed")
			
			blocked_by = false
			requested_by = false
		}
		
	}
}
