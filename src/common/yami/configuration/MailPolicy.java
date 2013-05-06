package yami.configuration;

public enum MailPolicy implements ActivePredicate
{
	NewFailure
	{
		@Override
		public boolean isActive(boolean before, boolean after)
		{
			return before == true && after == false;
		}
	},
	EachFailure
	{
		@Override
		public boolean isActive(boolean before, boolean after)
		{
			return after == false;
		}
	},
	BackToNormal
	{
		@Override
		public boolean isActive(boolean before, boolean after)
		{
			return before == false && after == true;
		}
	},
	EachRun
	{
		@Override
		public boolean isActive(boolean before, boolean after)
		{
			return true;
		}
	},
	Never
	{
		@Override
		public boolean isActive(boolean before, boolean after)
		{
			return false;
		}
	}
	;
	
}
