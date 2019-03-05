<%@include file="includes/taglib.jsp"%>
<!-- body content starts here -->
<div class="content-wrapper">
	<div>
		<section class="content-header">
			<h1 class="icglable_systemconfiguration"></h1>
		</section>
		<section class="content-header">
			<p
				class="bread_heading_parent sub_title icglable_scheduleconfiguration"></p>
		</section>
		<!-- body  content -->
		<section class="content">
			<section>
				<div class="row">
					<div class="col-md-12">
						<section class="user_account_mangmet">
							<p class="txt-12 text-center" id="submitMsg"></p>
							<div class="row">
								<div class="col-md-4">
									<div class="form-groups">
										<label for="adminScheduleDataSync"
											class="control-label icglable_environmentdatasync"></label> <select
											class="form-control-user" id="adminScheduleDataSync"
											name="adminScheduleDataSync">
											<option>0</option>
											<option>1</option>
											<option>2</option>
											<option>3</option>
											<option>4</option>
											<option>5</option>
											<option>6</option>
											<option>7</option>
											<option>8</option>
											<option>9</option>
											<option>10</option>
											<option>11</option>
											<option>12</option>
											<option>13</option>
											<option>14</option>
											<option>15</option>
											<option>16</option>
											<option>17</option>
											<option>18</option>
											<option>19</option>
											<option>20</option>
											<option>21</option>
											<option>22</option>
											<option>23</option>
										</select>
									</div>
								</div>
								<div class="col-md-4">
									<div class="form-group">
										<label for="adminScheduleSessionValidity"
											class="control-label icglabel_devicesessionvalidity"></label><select
											class="form-control-user" id="adminScheduleSessionValidity"
											name="adminScheduleSessionValidity" required
											title="Please enter Session Validity" tabindex="4">
											<option>1</option>
											<option>2</option>
											<option>3</option>
											<option>4</option>
											<option>5</option>
											<option>6</option>
											<option>7</option>
											<option>8</option>
											<option>9</option>
											<option>10</option>
											<option>11</option>
											<option>12</option>
											<option>13</option>
											<option>14</option>
											<option>15</option>
											<option>16</option>
											<option>17</option>
											<option>18</option>
											<option>19</option>
											<option>20</option>
											<option>21</option>
											<option>22</option>
											<option>23</option>
											<option>24</option>
											<option>25</option>
											<option>26</option>
											<option>27</option>
											<option>28</option>
											<option>29</option>
											<option>30</option>
											<option>31</option>
											<option>32</option>
											<option>33</option>
											<option>34</option>
											<option>35</option>
											<option>36</option>
											<option>37</option>
											<option>38</option>
											<option>39</option>
											<option>40</option>
											<option>41</option>
											<option>42</option>
											<option>43</option>
											<option>44</option>
											<option>45</option>
											<option>46</option>
											<option>47</option>
											<option>48</option>
											<option>49</option>
											<option>50</option>
											<option>51</option>
											<option>52</option>
											<option>53</option>
											<option>54</option>
											<option>55</option>
											<option>56</option>
											<option>57</option>
											<option>58</option>
											<option>59</option>
											<option>60</option>
										</select>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-md-4">
									<div class="form-groups">
										<label for="adminScheduleUserSessionValidity"
											class="control-label icglabel_webusersessionvalidity"></label>
										<select class="form-control-user"
											id="adminScheduleUserSessionValidity"
											name="adminScheduleUserSessionValidity" required
											title="Please Select Web user session validity" tabindex="3">
											<option>1</option>
											<option>2</option>
											<option>3</option>
											<option>4</option>
											<option>5</option>
											<option>6</option>
											<option>7</option>
											<option>8</option>
											<option>9</option>
											<option>10</option>
											<option>11</option>
											<option>12</option>
											<option>13</option>
											<option>14</option>
											<option>15</option>
											<option>16</option>
											<option>17</option>
											<option>18</option>
											<option>19</option>
											<option>20</option>
											<option>21</option>
											<option>22</option>
											<option>23</option>
											<option>24</option>
											<option>25</option>
											<option>26</option>
											<option>27</option>
											<option>28</option>
											<option>29</option>
											<option>30</option>
											<option>31</option>
											<option>32</option>
											<option>33</option>
											<option>34</option>
											<option>35</option>
											<option>36</option>
											<option>37</option>
											<option>38</option>
											<option>39</option>
											<option>40</option>
											<option>41</option>
											<option>42</option>
											<option>43</option>
											<option>44</option>
											<option>45</option>
											<option>46</option>
											<option>47</option>
											<option>48</option>
											<option>49</option>
											<option>50</option>
											<option>51</option>
											<option>52</option>
											<option>53</option>
											<option>54</option>
											<option>55</option>
											<option>56</option>
											<option>57</option>
											<option>58</option>
											<option>59</option>
											<option>60</option>
										</select>
									</div>
								</div>
								<div class="col-md-4">
									<div class="form-groups">
										<label for="adminSchedulePwdLinkValidity"
											class="control-label icglabel_passwordresetlinkvalidity"></label>
										<select class="form-control-user"
											id="adminSchedulePwdLinkValidity"
											name="adminSchedulePwdLinkValidity" required
											title="Please Select Password reset link validity"
											tabindex="5">
											<option>1</option>
											<option>2</option>
											<option>3</option>
											<option>4</option>
											<option>5</option>
											<option>6</option>
											<option>7</option>
											<option>8</option>
											<option>9</option>
											<option>10</option>
											<option>11</option>
											<option>12</option>
											<option>13</option>
											<option>14</option>
											<option>15</option>
											<option>16</option>
											<option>17</option>
											<option>18</option>
											<option>19</option>
											<option>20</option>
											<option>21</option>
											<option>22</option>
											<option>23</option>
											<option>24</option>
											<option>25</option>
											<option>26</option>
											<option>27</option>
											<option>28</option>
											<option>29</option>
											<option>30</option>
											<option>31</option>
											<option>32</option>
											<option>33</option>
											<option>34</option>
											<option>35</option>
											<option>36</option>
											<option>37</option>
											<option>38</option>
											<option>39</option>
											<option>40</option>
											<option>41</option>
											<option>42</option>
											<option>43</option>
											<option>44</option>
											<option>45</option>
											<option>46</option>
											<option>47</option>
											<option>48</option>
											<option>49</option>
											<option>50</option>
											<option>51</option>
											<option>52</option>
											<option>53</option>
											<option>54</option>
											<option>55</option>
											<option>56</option>
											<option>57</option>
											<option>58</option>
											<option>59</option>
											<option>60</option>
										</select>
									</div>
								</div>
							</div>
							<div class="row">
								<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
									<input type="button" class="save form-group" value="Save"
										id="adminScheduleSave" tabindex="6" />
								</div>
								<div class="col-lg-3 col-md-4 col-sm-6 col-xs-12">
									<input type="reset" class="cancel form-group" value="Cancel"
										id="adminScheduleCancel" tabindex="7" />
								</div>

							</div>
						</section>
					</div>
				</div>
			</section>
		</section>
	</div>
</div>
<script src="resources/js/adminSystemConfiguration.js"></script>
